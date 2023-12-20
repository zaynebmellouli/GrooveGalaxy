import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import proj.CL;
import org.junit.jupiter.api.Assertions;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

class CLTest extends CL {

    @org.junit.jupiter.api.Test
    void testGenerateAndReadAESKey() throws GeneralSecurityException, IOException {
        Key r = CL.generateAESKey("Keys/Key_Family_Musterman.key");
        Key t = CL.readAESKey("Keys/Key_Family_Musterman.key");
        Assertions.assertEquals(r, t);
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectFirst() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");

        JsonObject r = CL.protect(message.getBytes(), nonce, id, key);

        JsonObject     t = CL.unprotect(r, key);
        String M = new String(Base64.getDecoder().decode(t.get("M").getAsString()));

        Assertions.assertEquals(message, M);
        Assertions.assertTrue(check(t.get("M").getAsString(), t.get("ID").getAsInt(), Base64.getDecoder().decode(t.get("Nonce").getAsString()), key, t.get("MAC").getAsString()));
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectSecond() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        CL.generateAESKey("Keys/AESKeytest.key");
        CL.generateAESKey("Keys/AESFamilyKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");
        Key key_f = CL.readAESKey("Keys/AESFamilyKeytest.key");


        JsonObject r = CL.protect(message.getBytes(), nonce, key, key_f);

        JsonObject     t = CL.unprotect("CBC", r, key, nonce);
        String M = new String(Base64.getDecoder().decode(t.get("M").getAsString()));
        Key k_f = new SecretKeySpec(Base64.getDecoder().decode(t.get("Key_f").getAsString()), "AES");


        Assertions.assertEquals(message, M);
        Assertions.assertEquals(key_f, k_f);
        Assertions.assertTrue(check(t.get("M").getAsString(), nonce, key, r.get("MAC").getAsString()));
    }
    @org.junit.jupiter.api.Test
    void testProtectUnprotectThird() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");


        JsonObject r = CL.protect("CBC", message.getBytes(), nonce, key);

        JsonObject     t = CL.unprotect("CBC", r, key, nonce);
        String M =new String(Base64.getDecoder().decode(t.get("M").getAsString()));;


        Assertions.assertEquals(message, M);
        Assertions.assertTrue(check( t.get("M").getAsString(), nonce, key, r.get("MAC").getAsString()));


    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectFourth() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "This is the Music of the Night";
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");


        JsonObject r = CL.protect("CTR", message.getBytes(), nonce, key);

        JsonObject     t = CL.unprotect("CTR", r, key, nonce);
        String M = new String(Base64.getDecoder().decode(t.get("M").getAsString()));


        Assertions.assertEquals(message, M);
        Assertions.assertTrue(check(t.get("M").getAsString(), nonce, key, r.get("MAC").getAsString()));
    }


    //Attacks !!!
    @org.junit.jupiter.api.Test
    void testProtectUnprotectFirstAttackCypherM() {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        Key key = null;
        try {
            CL.generateAESKey("Keys/AESKeytest.key");
            key = CL.readAESKey("Keys/AESKeytest.key");
        } catch (IOException | GeneralSecurityException e) {
            Assertions.fail("Key generation or reading failed", e);
        }

        JsonObject r = null;
        try {
            r = CL.protect(message.getBytes(), nonce, id, key);
        } catch (GeneralSecurityException e) {
            Assertions.fail("Protection failed", e);
        }

        // Attack by modifying the encrypted message
        r.addProperty("Crypt_M", Base64.getEncoder().encodeToString("Hello world?".getBytes()));
        final Key finalKey = key;
        final JsonObject finalR = r;

        // Attempt to unprotect and check if the appropriate exception is thrown
        Assertions.assertThrows(GeneralSecurityException.class, () -> {
            JsonObject t = CL.unprotect(finalR, finalKey);
            // If the exception is thrown, the following lines will not be executed
            String M = t.get("M").getAsString();
            Assertions.assertNotEquals(message, M);
        });
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectFirstAttackID() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");

        JsonObject r = CL.protect(message.getBytes(), nonce, id, key);

        //Attack
        r.addProperty("ID", 6);

        JsonObject     t = CL.unprotect(r, key);
        String M = t.get("M").getAsString();
        Assertions.assertFalse(check(M, t.get("ID").getAsInt(), Base64.getDecoder().decode(t.get("Nonce").getAsString()), key, t.get("MAC").getAsString()));
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectSecondAttackCypherM() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        CL.generateAESKey("Keys/AESKeytest.key");
        CL.generateAESKey("Keys/AESFamilyKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");
        Key key_f = CL.readAESKey("Keys/AESFamilyKeytest.key");


        JsonObject r = CL.protect(message.getBytes(), nonce, key, key_f);

        // Attack by modifying the encrypted message
        r.addProperty("Crypt_M", Base64.getEncoder().encodeToString("Hello world?".getBytes()));


        final Key finalKey = key;
        final JsonObject finalR = r;
        final byte[] finalNonce = nonce;

        // Attempt to unprotect and check if the appropriate exception is thrown
        Assertions.assertThrows(GeneralSecurityException.class, () -> {
            JsonObject t = CL.unprotect("CBC",finalR, finalKey,finalNonce);
            // If the exception is thrown, the following lines will not be executed
            String M = t.get("M").getAsString();
            Assertions.assertNotEquals(message, M);
            Key k_f = new SecretKeySpec(Base64.getDecoder().decode(t.get("Key_f").getAsString()), "AES");
            Assertions.assertEquals(key_f, k_f);
        });
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectSecondAttackK_f() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        CL.generateAESKey("Keys/AESKeytest.key");
        CL.generateAESKey("Keys/AESFamilyKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");
        Key key_f = CL.readAESKey("Keys/AESFamilyKeytest.key");


        JsonObject r = CL.protect(message.getBytes(), nonce, key, key_f);

        // Attack by modifying the encrypted message
        r.addProperty("Crypt_Key_f", Base64.getEncoder().encodeToString("Hello".getBytes()));


        final Key finalKey = key;
        final JsonObject finalR = r;
        final byte[] finalNonce = nonce;

        // Attempt to unprotect and check if the appropriate exception is thrown
        Assertions.assertThrows(GeneralSecurityException.class, () -> {
            JsonObject t = CL.unprotect("CBC",finalR, finalKey,finalNonce);
            // If the exception is thrown, the following lines will not be executed
            String M = t.get("M").getAsString();
            Assertions.assertEquals(message, M);
            Key k_f = new SecretKeySpec(Base64.getDecoder().decode(t.get("Key_f").getAsString()), "AES");
            Assertions.assertNotEquals(key_f, k_f);
        });
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectFourthStreamPart() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
//        random.nextBytes(nonce);

        String message = "This is the Music of the Night";
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");

        // Protect the message using AES CTR mode
        JsonObject r = CL.protect("CTR", message.getBytes(), nonce, key);

        // Simulate streaming by decrypting from the 4th byte
        int offset = 1; // Assuming the first byte is at index 0
        byte[] encryptedMessage = Base64.getDecoder().decode(r.get("Crypt_M").getAsString());
        byte[] partialEncryptedMessage = new byte[encryptedMessage.length - offset * 16];
        System.arraycopy(encryptedMessage, offset * 16, partialEncryptedMessage, 0, partialEncryptedMessage.length);

        // You need to adjust the nonce to account for the offset in CTR mode
        byte[] adjustedNonce = incrementCounterInNonce(nonce, offset ); // Assuming each byte is 2 hex characters

        System.out.println("Nonce: " + Base64.getEncoder().encodeToString(nonce));
        System.out.println("Adjusted nonce: " + Base64.getEncoder().encodeToString(adjustedNonce));
        String expectedPartialMessage = message.substring(offset * 16);

        r.addProperty("Crypt_M", Base64.getEncoder().encodeToString(partialEncryptedMessage));
        r.addProperty("MAC", CL.calculateMAC(expectedPartialMessage, adjustedNonce, key));

        // Unprotect with the adjusted nonce and partial message
        JsonObject t = CL.unprotect("CTR", r, key, adjustedNonce);
        String M = new String(Base64.getDecoder().decode(t.get("M").getAsString()));

        // Since we're starting from the 4th byte, we need to compare with the corresponding substring of the message

        System.out.println("Partial message: " + M);
        System.out.println("Expected partial message: " + expectedPartialMessage);

        Assertions.assertEquals(expectedPartialMessage, M);
        Assertions.assertTrue(check(new String(Base64.getDecoder().decode(t.get("M").getAsString())), adjustedNonce, key, t.get("MAC").getAsString()));
    }
//    @Test
//    void testProtectUnprotectFourthStreamPart() throws IOException, GeneralSecurityException {
//        SecureRandom random = new SecureRandom();
//        byte[] nonce = new byte[16];
//        random.nextBytes(nonce);
//
//        String    message = "This is the Music of the Night";
//        SecretKey key     = KeyGenerator.getInstance("AES").generateKey(); // Generate key for testing
//
//        // Protect the message using AES CTR mode and write to a file
//        String encryptedFilePath = "encryptedMessage.bin";
//        CL.protectToFile("CTR", message, nonce, key, encryptedFilePath);
//
//        // Offset from which to start decryption
//        int offset = 3; // Starting from the 4th byte
//
//        // Unprotect with streaming decryption starting from the offset
//        String decryptedMessage = CL.unprotectFromFileWithOffset("CTR", encryptedFilePath, key, nonce, offset);
//
//        // Expected result is a substring of the original message starting from the offset
//        String expectedPartialMessage = message.substring(offset);
//
//        Assertions.assertEquals(expectedPartialMessage, decryptedMessage);
//    }
}