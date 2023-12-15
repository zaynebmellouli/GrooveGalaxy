import com.google.gson.JsonObject;
import proj.CL;
import org.junit.jupiter.api.Assertions;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

class CLTest extends CL {

    @org.junit.jupiter.api.Test
    void testGenerateAndReadAESKey() throws GeneralSecurityException, IOException {
        Key r = CL.generateAESKey("Keys/AESKeytest.key");
        Key t = CL.readAESKey("Keys/AESKeytest.key");
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

        JsonObject r = CL.protect(message, nonce, id, key);

        JsonObject     t = CL.unprotect(r, key);
        String M = t.get("M").getAsString();

        Assertions.assertEquals(message, M);
    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectSecond() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        CL.generateAESKey("Keys/AESKeytest.key");
        CL.generateAESKey("Keys/AESFamilyKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");
        Key key_f = CL.readAESKey("Keys/AESFamilyKeytest.key");


        JsonObject r = CL.protect(message, nonce, key, key_f);

        JsonObject     t = CL.unprotect("CBC", r, key, nonce);
        String M = t.get("M").getAsString();
        Key k_f = new SecretKeySpec(Base64.getDecoder().decode(t.get("Key_f").getAsString()), "AES");


        Assertions.assertEquals(message, M);
        Assertions.assertEquals(key_f, k_f);

    }
    @org.junit.jupiter.api.Test
    void testProtectUnprotectThird() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");


        JsonObject r = CL.protect("CBC", message, nonce, key);

        JsonObject     t = CL.unprotect("CBC", r, key, nonce);
        String M = t.get("M").getAsString();


        Assertions.assertEquals(message, M);

    }

    @org.junit.jupiter.api.Test
    void testProtectUnprotectFourth() throws IOException, GeneralSecurityException {
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[16];
        random.nextBytes(nonce);

        String message = "Hello world!";
        int id = 1;
        CL.generateAESKey("Keys/AESKeytest.key");
        Key key = CL.readAESKey("Keys/AESKeytest.key");


        JsonObject r = CL.protect("CBC", message, nonce, key);

        JsonObject     t = CL.unprotect("CBC", r, key, nonce);
        String M = t.get("M").getAsString();


        Assertions.assertEquals(message, M);

    }
}