package proj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import static proj.CL.check;
import static proj.CL.incrementByteNonce;

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
            int id = 0;
            byte[] nonce = new byte[16];
            try (Socket socket = listener.accept()) {

                while (!message.equals("Exit")) {
                    try {
                        is = new BufferedInputStream(socket.getInputStream());
                        byte[] data = new byte[2048];
                        //first message
                        int len = is.read(data);
                        if (len == -1) {return;}

                        message = new String(data, 0, len);
                        JsonObject receivedJson1 = JsonParser.parseString(message).getAsJsonObject();
                        //get the key of the client from the id form the json
                         id = receivedJson1.get("id").getAsInt();
                         nonce = new byte[]{receivedJson1.get("nonce").getAsByte()};
                         //to change the key to the key in the database
                         Key keyServClient = CL.readAESKey("Keys/KeyServClient.key");
                         Key keyFamily     = CL.readAESKey("Keys/KeyFamily.key");
                        //decrypt the message
                        JsonObject decryptedJson1 = CL.unprotect(receivedJson1, keyServClient);
                        String Song = decryptedJson1.get("M").getAsString();
                        if(check(Song,id,nonce, keyServClient,receivedJson1.get("MAC").getAsString())){
                                // Check if the message is an error message
                                if (Song.equalsIgnoreCase("error")) {
                                    System.out.println("Error message received. Aborting communication.");
                                    // Close the connection
                                    socket.close();
                                    return;
                                } else {
                                    System.out.printf("Client received %d bytes: %s%n", len, message);
                                    // Continue to send the second message
                                }
                            }else {
                                System.out.println("Integrity check failed. Sending error message to client.");
                                // Send error message to server
                                String errorMessage = "Error";
                                nonce = incrementByteNonce(nonce);
                            JsonObject r = CL.protect("CBC",errorMessage, nonce, keyServClient);
                            //HELP - the client is expecting a concatinated message with the family fey
                            byte[] messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                // Close the connection
                                socket.close();
                                return;
                            }



                        //Contact Database for the Song Info
                        //Check for Song in the Database

                        //Respond to first message
                        message = "Song Info";
                        JsonObject r = CL.protect(message, nonce, keyServClient, keyFamily);
                        byte[] messageBytes = r.toString().getBytes();
                        os = new BufferedOutputStream(socket.getOutputStream());
                        os.write(messageBytes);
                        os.flush();

                        //Listen for Response
                        len = is.read(data);
                        if (len == -1) {return;}
                        message = new String(data, 0, len);
                        JsonObject receivedJson2 = JsonParser.parseString(message).getAsJsonObject();
                        nonce = incrementByteNonce(nonce);
                        //decrypt the message
                        JsonObject decryptedJson2 = CL.unprotect(receivedJson2, keyServClient);
                        String byteReq = decryptedJson2.get("M").getAsString();
                        if(check(byteReq,id,nonce, keyServClient,receivedJson2.get("MAC").getAsString())){
                            // Check if the message is an error message
                            if (byteReq.equalsIgnoreCase("error")) {
                                System.out.println("Error message received. Aborting communication.");
                                // Close the connection
                                socket.close();
                                return;
                            } else {
                                System.out.printf("Client received %d bytes: %s%n", len, message);
                                // Continue to send the second message
                            }
                        }else {
                            System.out.println("Integrity check failed. Sending error message to client.");
                            // Send error message to server
                            String errorMessage = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect("CBC",errorMessage, nonce, keyServClient);
                            //HELP - the client is expecting a concatinated message with the family fey
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            // Close the connection
                            socket.close();
                            return;
                        }



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
        System.setProperty("javax.net.ssl.keyStore", "https_cert/server.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/servertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        int port = 5000;
        startServer(port);
    }
}
