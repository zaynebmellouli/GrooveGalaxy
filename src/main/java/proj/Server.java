package proj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

    public static void startServer(int port) throws IOException {

        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        try (SSLServerSocket listener = (SSLServerSocket) factory.createServerSocket(port)) {
            listener.setNeedClientAuth(true);
            listener.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            listener.setEnabledProtocols(new String[] { "TLSv1.3" });
            System.out.println("listening for messages...");
            String message = null;
            InputStream is = null;
            OutputStream os = null;

            try (Socket socket = listener.accept()) {

                while (!message.equals("Exit")) {
                    try {
                        is = new BufferedInputStream(socket.getInputStream());
                        byte[] data = new byte[2048];
                        //first message
                        int len = is.read(data);

                        message = new String(data, 0, len);
                        JsonObject jsonEncrypted = JsonParser.parseString(message).getAsJsonObject();

                        //get the key of the client from the id form the json
                         int id = jsonEncrypted.get("id").getAsInt();
                         //to change the key to the key in the database
                         Key keyServClient = CL.readAESKey("Keys/KeyServClient.key");

                        //decrypt the message
                        JsonObject jsonDecrypt = CL.unprotect(jsonEncrypted, keyServClient);





                        os = new BufferedOutputStream(socket.getOutputStream());
                        System.out.printf("server received %d bytes: %s%n", len, message);
                        String response = message + " processed by server";
                        os.write(response.getBytes(), 0, response.getBytes().length);
                        os.flush();
                    } catch (IOException i) {
                        System.out.println(i);
                        return;
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        System.out.println("Error decrypting message, maybe hacked");
                        os = new BufferedOutputStream(socket.getOutputStream());
                        System.out.printf("Sending error message");
                        String response = "Error";
                        os.write(response.getBytes(), 0, response.getBytes().length);
                        os.flush();
                    }
                }
                try {
                    is.close();
                    os.close();
                    socket.close();
                } catch (IOException i) {
                    System.out.println(i);
                    return;
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "proj/https_cert/database.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "proj/https_cert/databasetruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        int port = 5000;
        startServer(port);
    }
}
