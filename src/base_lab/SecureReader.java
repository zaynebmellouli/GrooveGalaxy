package pt.tecnico;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.crypto.Cipher;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import static pt.tecnico.CryptoExample.*;

public class SecureReader {
    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s file%n", JsonReader.class.getName());
            return;
        }
        final String filename = args[0];

        // Read JSON object from file, and print its contets
        try (FileReader fileReader = new FileReader(filename)) {
            Gson       gson     = new Gson();
            JsonObject rootJsonC = gson.fromJson(fileReader, JsonObject.class);

            //decipher
            Key key        = readSecretKey("keys/secret.key");
            Cipher  cipher     = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] cipherBytes = cipher.doFinal(Base64.getDecoder().decode(rootJsonC.get("body").getAsString()));
            JsonObject  rootJson = gson.fromJson(new String(cipherBytes), JsonObject.class);


            System.out.println("JSON object: " + rootJson);

            JsonObject headerObject = rootJson.get("header").getAsJsonObject();
            System.out.println("Document header:");
            System.out.println("Author: " + headerObject.get("author").getAsString());
            System.out.println("Version: " + headerObject.get("version").getAsInt());
            System.out.println("Title: " + headerObject.get("title").getAsString());
            JsonArray tagsArray = headerObject.getAsJsonArray("tags");
            System.out.print("Tags: ");
            for (int i = 0; i < tagsArray.size(); i++) {
                System.out.print(tagsArray.get(i).getAsString());
                if (i < tagsArray.size() - 1) {
                    System.out.print(", ");
                } else {
                    System.out.println(); // Print a newline after the final tag
                }
            }

            System.out.println("Document body: " + rootJson.get("body").getAsString());

            JsonObject statusObject = rootJson.get("status").getAsJsonObject();
            System.out.println("Status:");
            System.out.println("Draft: " + statusObject.get("draft").getAsBoolean());
            System.out.println("Published: " + statusObject.get("published").getAsBoolean());
            System.out.println("Archived: " + statusObject.get("archived").getAsBoolean());

            PublicKey pub = readPublicKey("keys/alice.pubkey");
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initVerify(pub);
            String sig = rootJson.get("MAC").getAsString();
            rootJson.remove("MAC");
            byte[] rootJsonMod = rootJson.toString().getBytes();
            rsa.update(rootJsonMod);

            System.out.println("Secure: " + rsa.verify(Base64.getDecoder().decode(sig)));
        }
    }
}
