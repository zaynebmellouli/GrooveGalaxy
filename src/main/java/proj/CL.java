package proj;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
        String data = message + Base64.getEncoder().encodeToString(nonce);

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

    /**
     * Protect for first req of client in CBC mode
     * @param message the requested music
     * @param nonce the first nonce to use
     * @param ID the ID of the client
     * @param symKey_c  the symmetric key of the client that he shares with the server
     * @return a json object with (MAC(M, ID, N), Crypt(M), nonce, ID)
     * @throws GeneralSecurityException if the cipher is not initialized correctly
     */
    public static JsonObject protect(byte[] message, byte[] nonce, int ID, Key symKey_c) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }

        // Initialize the cipher in CTR mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);

        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(Base64.getEncoder().encodeToString(message), ID, nonce, symKey_c));
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message)));
        result.addProperty("Nonce", Base64.getEncoder().encodeToString(nonce));
        result.addProperty("ID", ID);

        return result;
    }

    /**
     * Protect for the message of the client and the stream of the music in CTR mode
     * @param message the requested music
     * @param nonce the next nonce to use
     * @param symKey_c  the symmetric key of the client that he shares with the server
     * @return a json object with (MAC(M, N), Crypt(M))
     * @throws GeneralSecurityException if the cipher is not initialized correctly
     */
    public static JsonObject protect(byte[] message, byte[] nonce, Key symKey_c) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ivSpec = new IvParameterSpec(nonce);

        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);
        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(Base64.getEncoder().encodeToString(message), nonce, symKey_c));
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message)));
        return result;
    }


    public static byte[] protectCTR( byte[] message, byte[] nonce, Key symKey_c) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        IvParameterSpec ivSpec = new IvParameterSpec(nonce);

        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);

        return cipher.doFinal(message);
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
    public static JsonObject protect(byte[] message, byte[] nonce, Key symKey_c, Key symKey_f) throws GeneralSecurityException {
        // Ensure nonce is the correct size (16 bytes for AES)
        if (nonce.length != 16) {
            throw new IllegalArgumentException("Nonce must be 16 bytes long");
        }

        // Initialize the cipher in CTR mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.ENCRYPT_MODE, symKey_c, ivSpec);

        JsonObject result = new JsonObject();
        result.addProperty("MAC", calculateMAC(Base64.getEncoder().encodeToString(message), nonce, symKey_c));
        result.addProperty("Crypt_Key_f", Base64.getEncoder().encodeToString(cipher.doFinal(symKey_f.getEncoded())));
        cipher.init(Cipher.ENCRYPT_MODE, symKey_f, ivSpec);
        result.addProperty("Crypt_M", Base64.getEncoder().encodeToString(cipher.doFinal(message)));

        return result;
    }

    /**
     * Unprotect the first message of the client in CBC mode where he gives the nonce in the json
     * @param json the json object with (MAC(M, ID, N), Crypt(M), nonce, ID)
     * @param symKey the symmetric key of the client that he shares with the server
     * @return the json object with M
     * @throws GeneralSecurityException
     */
    public static JsonObject unprotect(JsonObject json, Key symKey) throws GeneralSecurityException {
        // Extract the necessary properties from the JSON object
        byte[] encryptedM = Base64.getDecoder().decode(json.get("Crypt_M").getAsString());
        byte[] nonce = Base64.getDecoder().decode(json.get("Nonce").getAsString());

        // Initialize the cipher for decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);

        // Decrypt the data
        json.addProperty("M", Base64.getEncoder().encodeToString(cipher.doFinal(encryptedM)));
        return json;
    }

    /**
     * Unprotect the message different modes depending on the json received
s     * @param json the json object with different kind of information depending on the mode
     * @param symKey the symmetric key of the client that he shares with the server
     * @param nonce the nonce to use
     * @return the json object with encrypted message in M or in Key_f
     * @throws GeneralSecurityException
     */
    public static JsonObject unprotect(JsonObject json, Key symKey, byte[] nonce) throws GeneralSecurityException {
        // Extract the necessary properties from the JSON object
        byte[] encryptedM = Base64.getDecoder().decode(json.get("Crypt_M").getAsString());
        byte[] encryptedKey_f = json.has("Crypt_Key_f") ? Base64.getDecoder().decode(json.get("Crypt_Key_f").getAsString()): null;

        // Initialize the cipher for decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);
        cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);
        if (encryptedKey_f != null){
            // Decrypt the data
            Key key_f = new SecretKeySpec(cipher.doFinal(encryptedKey_f), "AES");
            json.addProperty("Key_f", Base64.getEncoder().encodeToString(key_f.getEncoded()));

            cipher.init(Cipher.DECRYPT_MODE, key_f, ivSpec);
            json.addProperty("M", Base64.getEncoder().encodeToString(cipher.doFinal(encryptedM)));
            return json;

        } else {
                // Decrypt the data
                json.addProperty("M", Base64.getEncoder().encodeToString(cipher.doFinal(encryptedM)));
                return json;
        }

    }

    public static byte[] unprotectCTR(byte[] encryptedM, Key symKey, byte[] nonce) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);

        cipher.init(Cipher.DECRYPT_MODE, symKey, ivSpec);

        // Decrypt the data
        return cipher.doFinal(encryptedM);
    }




    public static boolean check(String message , byte[] nonce, Key key, String macToCheck) throws NoSuchAlgorithmException, InvalidKeyException  {
        // Calculate the MAC based on the message, nonce, and key
        String calculatedMac = calculateMAC(message, nonce, key);

        // Compare the calculated MAC with the given MAC
        return calculatedMac.equalsIgnoreCase(macToCheck);
    }

    public static boolean check(String message, int id, byte[] nonce, Key key, String macToCheck) throws NoSuchAlgorithmException, InvalidKeyException  {
        // Calculate the MAC based on the message, nonce, and key
        String calculatedMac = calculateMAC(message,id, nonce, key);

        // Compare the calculated MAC with the given MAC
        return calculatedMac.equalsIgnoreCase(macToCheck);
    }

    public static byte[] incrementCounterInNonce(byte[] nonce, int offset) {
        // Copy the original nonce to avoid mutating it
        byte[] adjustedNonce = Arrays.copyOf(nonce, nonce.length);

        // Convert the last 4 bytes of the nonce to an integer
        ByteBuffer byteBuffer = ByteBuffer.wrap(adjustedNonce, nonce.length - 4, 4);
        byteBuffer.order(ByteOrder.BIG_ENDIAN); // use BIG_ENDIAN if the nonce is in network byte order
        int counterValue = byteBuffer.getInt();

        // Increment the counter by the number of blocks offset
        counterValue += offset / 16; // AES block size is 16 bytes


        // Put the incremented counter back into the nonce
        byteBuffer.position(nonce.length - 4);
        byteBuffer.putInt(counterValue);

        return adjustedNonce;
    }

    // Method to increment a byte array nonce
    public static byte[] incrementByteNonce(byte[] n) {
        byte[] nonce = n;
        for (int i = nonce.length - 1; i >= 0; i--) {
            if (++nonce[i] != 0) break; // Overflow check
        }
        return nonce;
    }

}

