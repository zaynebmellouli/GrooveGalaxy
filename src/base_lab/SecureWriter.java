package pt.tecnico;

import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;

import com.google.gson.*;

import javax.crypto.Cipher;

import static pt.tecnico.CryptoExample.*;

public class SecureWriter {
    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
//            System.err.printf("Usage: java %s file%n", JsonWriter.class.getName());
            return;
        }
        final String filename = args[0];

        // Create bank statement JSON object
        JsonObject jsonObject = new JsonObject();

        JsonObject headerObject = new JsonObject();
        headerObject.addProperty("author", "Ultron");
        headerObject.addProperty("version", 2);
        headerObject.addProperty("title", "Avengers: Age of Ultron");
        JsonArray tagsArray = new JsonArray();
        tagsArray.add("robot");
        tagsArray.add("autonomy");
        headerObject.add("tags", tagsArray);
        jsonObject.add("header", headerObject);

        jsonObject.addProperty("body", "I had strings but now I'm free");

        JsonObject statusObject = new JsonObject();
        statusObject.addProperty("draft", true);
        statusObject.addProperty("published", false);
        statusObject.addProperty("archived", false);

        jsonObject.add("status",statusObject);


        PrivateKey priv = readPrivateKey("keys/alice.privkey");

        Signature rsa = Signature.getInstance("SHA1withRSA");

        rsa.initSign(priv);

        rsa.update(jsonObject.toString().getBytes());


        jsonObject.addProperty("MAC", Base64.getEncoder().encodeToString(rsa.sign()));



        //ciphering
        Key key = readSecretKey("keys/secret.key");
        byte[] plainBytes = jsonObject.toString().getBytes();
        Cipher cipher     = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(plainBytes);
        JsonObject jsonObjectC = new JsonObject();
        jsonObjectC.addProperty("body", Base64.getEncoder().encodeToString(cipherBytes));


        // Write JSON object to file
        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObjectC, fileWriter);
        }
    }
}
