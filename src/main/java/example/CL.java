package example;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import com.google.gson.*;


/**
 * Generate AES key
 */
public class CL {


    public static Key generateAESKey(String keyPath) throws GeneralSecurityException, IOException {
        System.out.println("Generating AES key ...");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // Use 192 or 256 for stronger encryption
        Key key = keyGen.generateKey();
        System.out.println("Finish generating AES key");

        byte[] encoded = key.getEncoded();
        System.out.println("Writing key to '" + keyPath + "' ...");

        try (FileOutputStream fos = new FileOutputStream(keyPath)) {
            fos.write(encoded);
        }

        return key;
    }

    public static Key readAESKey(String keyPath) throws IOException {
        System.out.println("Reading key from file " + keyPath + " ...");
        File file = new File(keyPath);
        byte[] encoded = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(encoded);
        }

        return new SecretKeySpec(encoded, "AES");
    }


    /**
     * Calculate the HMAC of a message using the specified key with an id
     * @param message the message to calculate the HMAC of
     * @param id the id of the client
     * @param nonce the nonce to use
     * @param key  the key to use
     * @return the HMAC of the message in hex string format
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidKeyException if the key is invalid
     */
    public static String calculateMAC(String message, int id, byte[] nonce, Key key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // Concatenate message, ID, and nonce
        String data = message + id + new String(nonce);

        // Create a MAC instance using HMAC-SHA256 algorithm
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);

        // Compute the HMAC
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        // Convert the HMAC to a hex string
        return bytesToHex(hmacBytes);
    }

    /**
     * Calculate the HMAC of a message using the specified key with
     * @param message the message to calculate the HMAC of
     * @param nonce the nonce to use
     * @param key  the key to use
     * @return the HMAC of the message in hex string format
     * @throws NoSuchAlgorithmException if the algorithm is not found
     * @throws InvalidKeyException if the key is invalid
     */
    public static String calculateMAC(String message, byte[] nonce, Key key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        // Concatenate message, ID, and nonce
        String data = message + new String(nonce);

        // Create a MAC instance using HMAC-SHA256 algorithm
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);

        // Compute the HMAC
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        // Convert the HMAC to a hex string
        return bytesToHex(hmacBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length]; // Create a new array with combined length
        System.arraycopy(a, 0, result, 0, a.length);  // Copy first array into result array
        System.arraycopy(b, 0, result, a.length, b.length);  // Copy second array into result array starting at the end of the first array
        return result;
    }

    /**
     * Protect for first req of client in CBC mode
     * @param message the requested music
     * @param nonce the first nonce to use
     * @param ID the ID of the client
     * @param symKey_c  the symmetric key of the client that he shares with the server
     * @return a json object with (MAC(M, ID, N), Crypt(M), nonce, ID)
     * @throws GeneralSecurityException if the cipher is not initialized correctly
     */
    public static JsonObject protect(String message, byte[] nonce, int ID, Key symKey_c) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }

        // Initialize the cipher in CTR mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);

        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(message, ID, nonce, symKey_c));
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes())));
        result.addProperty("Nonce", Base64.getEncoder().encodeToString(nonce));
        result.addProperty("ID", ID);

        return result;
    }

    /**
     * Protect for the message of the client and the stream of the music in CTR mode
     * @param mode the mode of the cipher ("CBC" or "CTR")
     * @param message the requested music
     * @param nonce the next nonce to use
     * @param symKey_c  the symmetric key of the client that he shares with the server
     * @return a json object with (MAC(M, N), Crypt(M))
     * @throws GeneralSecurityException if the cipher is not initialized correctly
     */
    public static JsonObject protect(String mode, String message, byte[] nonce, Key symKey_c) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }
        Cipher cipher = Objects.equals(mode, "CTR") ? Cipher.getInstance("AES/CTR/NoPadding")
                                                        : Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);
        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(message, nonce, symKey_c));
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes())));
        return result;
    }

    /**
     * Protect for the first answer the server in CBC mode
     * @param message the requested music
     * @param nonce the next nonce to use
     * @param symKey_c  the symmetric key of the client that he shares with the server
     * @param symKey_f  the symmetric key of the family of the client
     * @return a json object with (MAC(M, N), Crypt(K_f) with K_C, Crypt(M) with K_f)
     * @throws GeneralSecurityException if the cipher is not initialized correctly
     */
    public static JsonObject protect(String message, byte[] nonce, Key symKey_c, Key symKey_f) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }

        // Initialize the cipher in CTR mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);

        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(message, nonce, symKey_c));
        result.addProperty("Crypt_Key_f", Base64.getEncoder().encodeToString(cipher.doFinal(symKey_f.getEncoded())));
        cipher.init(Cipher.ENCRYPT_MODE, symKey_f, ivSpec);
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes())));

        return result;
    }

    /**
     * Unprotect the first message of the client in CBC mode where he gives the nonce in the json
     * @param json the json object with (MAC(M, ID, N), Crypt(M), nonce, ID)
     * @param symKey the symmetric key of the client that he shares with the server
     * @return the json object with (M, ID)
     * @throws GeneralSecurityException
     */
    public static JsonObject unprotect(JsonObject json, Key symKey) throws GeneralSecurityException {
        // Extract the necessary properties from the JSON object
        byte[] encryptedM = Base64.getDecoder().decode(json.get("Crypt_M").getAsString());
        byte[] nonce = Base64.getDecoder().decode(json.get("Crypt_nonce").getAsString());

        // Initialize the cipher for decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);

        // Decrypt the data
        json.addProperty("M", new String(cipher.doFinal(encryptedM)));
        return json;
    }

    /**
     * Unprotect the message different modes depending on the json received
     * @param mode the mode of the cipher ("CBC" or "CTR")
     * @param json the json object with different kind of information depending on the mode
     * @param symKey the symmetric key of the client that he shares with the server
     * @param nonce the nonce to use
     * @return the json object with encrypted message in M or in Key_f
     * @throws GeneralSecurityException
     */
    public static JsonObject unprotect(String mode, JsonObject json, Key symKey, byte[] nonce) throws GeneralSecurityException {
        // Extract the necessary properties from the JSON object
        byte[] encryptedM = Base64.getDecoder().decode(json.get("Crypt_M").getAsString());
        if (mode.equals("CBC")) {
            byte[] encryptedKey_f = json.has("Crypt_Key_f") ? Base64.getDecoder().decode(json.get("Crypt_Key_f").getAsString()): null;

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(nonce);
            cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);
            if (encryptedKey_f != null){
                // Decrypt the data
                Key key_f = new SecretKeySpec(cipher.doFinal(encryptedKey_f), "AES");
                json.addProperty("Key_f", key_f.toString());

                cipher.init(Cipher.DECRYPT_MODE, key_f, ivSpec);
                json.addProperty("M", new String(cipher.doFinal(encryptedM)));
                return json;

            } else {
                    // Decrypt the data
                    json.addProperty("M", new String(cipher.doFinal(encryptedM)));
                    return json;
            }
        } else if (mode.equals("CTR")) {
            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            IvParameterSpec ivSpec = new IvParameterSpec(nonce);
            cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);

            // Decrypt the data
            json.addProperty("M", new String(cipher.doFinal(encryptedM)));
            return json;

        } else {
            throw new IllegalArgumentException("Mode must be CBC or CTR");
        }
    }
}

