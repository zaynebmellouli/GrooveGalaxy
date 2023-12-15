import example.CL;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

class CLTest extends CL {

    @org.junit.jupiter.api.Test
    void testGenerateAESKey() throws GeneralSecurityException, IOException {
        CL.generateAESKey("Keys/AESKeytest.key");
    }

    @org.junit.jupiter.api.Test
    void testReadAESKey() throws GeneralSecurityException, IOException {
        Key r = CL.generateAESKey("Keys/AESKeytest.key");
        Key t = CL.readAESKey("Keys/AESKeytest.key");
        Assertions.assertEquals(r, t);
    }

    @org.junit.jupiter.api.Test
    void testCalculateMAC() {
    }

    @org.junit.jupiter.api.Test
    void testCalculateMAC1() {
    }

    @org.junit.jupiter.api.Test
    void testConcatenateByteArrays() {
    }

    @org.junit.jupiter.api.Test
    void testProtect() {
    }

    @org.junit.jupiter.api.Test
    void testProtect1() {
    }

    @org.junit.jupiter.api.Test
    void testProtect2() {
    }

    @org.junit.jupiter.api.Test
    void testUnprotect() {
    }

    @org.junit.jupiter.api.Test
    void testUnprotect1() {
    }
}