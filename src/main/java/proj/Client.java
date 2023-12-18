package proj;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static proj.CL.*;


public class Client {

    public static void startClient(String host, int port) throws IOException {

        SocketFactory factory = SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

            socket.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            socket.setEnabledProtocols(new String[] { "TLSv1.3" });

            String message = "This is a secure channel!";
            System.out.println("sending message: " + message);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            InputStream is = new BufferedInputStream(socket.getInputStream());
            byte[] data = new byte[2048];
            int len = is.read(data);
            System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));
            
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Type: 'Exit' to close the connection");

                    try {
                        SecureRandom random = new SecureRandom();
                        byte[] nonce = new byte[16];
                        random.nextBytes(nonce);
                        int id = 1;
                        Key key = CL.readAESKey("Keys/KeyServClient.key");
                        Key key_f = CL.readAESKey("Keys/KeyFamily.key");

                        //First Message
                        message = "This Song";
                        JsonObject r = CL.protect(message, nonce, id, key);
                        byte[] messageBytes = r.toString().getBytes();
                        os = new BufferedOutputStream(socket.getOutputStream());
                        os.write(messageBytes);
                        os.flush();

                        //Listen for Response
                        is = new BufferedInputStream(socket.getInputStream());
                        data = new byte[2048];
                        len = is.read(data);
                        if (len != -1) {
                            String firstMessage = new String(data, 0, len);
                            JsonObject receivedJson1 = JsonParser.parseString(firstMessage).getAsJsonObject();
                            JsonObject decryptedJson1 = unprotect("CBC", receivedJson1, key, nonce);
                            if(check(decryptedJson1.get("M").getAsString(), nonce,key,receivedJson1.get("MAC").getAsString())){
                                // Check if the message is an error message
                                String m = decryptedJson1.get("M").getAsString();
                                if (m.equalsIgnoreCase("error")) {
                                    System.out.println("Error message received. Aborting communication.");
                                    // Close the connection
                                    socket.close();
                                    return;
                                } else {
                                    System.out.printf("Client received %d bytes: %s%n", len, m);
                                    // Continue to send the second message
                                }
                            }else {
                                System.out.println("Integrity check failed. Sending error message to server.");
                                // Send error message to server
                                String errorMessage = "Error";
                                nonce = incrementByteNonce(nonce);
                                r = CL.protect("CBC", errorMessage, nonce, key);
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                // Close the connection
                                socket.close();
                                return;
                            }
                        }
                        // Second message
                        message = "These Bytes";
                        nonce = incrementByteNonce(nonce);
                        r = CL.protect(message, nonce, key, key_f);
                        messageBytes = r.toString().getBytes();
                        os.write(messageBytes);
                        os.flush();

                        System.out.println("finished");
                        //Listen for Response
                        len = is.read(data);
                        if (len != -1) {
                            String secondMessage = new String(data, 0, len);
                            JsonObject receivedJson2 = JsonParser.parseString(secondMessage).getAsJsonObject();
                            JsonObject decryptedJson2 = unprotect("CTR", receivedJson2, key, nonce);
                            if(check(decryptedJson2.get("M").getAsString(), nonce,key,receivedJson2.get("MAC").getAsString())){
                                // Check if the message is an error message
                                String m = decryptedJson2.get("M").getAsString();
                                if (m.equalsIgnoreCase("error")) {
                                    System.out.println("Error message received. Aborting communication.");
                                    // Close the connection
                                    socket.close();
                                    return;
                                } else {
                                    System.out.printf("Client received %d bytes: %s%n", len, message);
                                }
                            }else {
                                System.out.println("Integrity check failed. Sending error message to server.");
                                // Send error message to server
                                String errorMessage = "Error";
                                nonce = incrementByteNonce(nonce);
                                r = CL.protect(message, nonce, key, key_f);
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                // Close the connection
                                socket.close();
                                return;
                            }
                        }


                    } catch (IOException i) {
                        System.out.println(i);
                        return;
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                }
            try {
                    //scanner.close();
                    is.close();
                    os.close();
                    socket.close();
                } catch (IOException i) {
                    System.out.println(i);
                    return;
                }
            }

        }



    public static void main(String args[]) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "https_cert/user.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/usertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        String host =
//                "localhost";
                "192.168.0.100";
        int port = 5000;
        startClient(host, port);
    }
}
