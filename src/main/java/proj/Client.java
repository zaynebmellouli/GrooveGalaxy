package proj;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import proj.server_client.data_objects.SecurityException;

import java.io.*;
import java.security.*;
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
                            if(!check(decryptedJson1.get("M").getAsString(), nonce,key,receivedJson1.get("MAC").getAsString())){
                                System.out.println("Error! Restarting conversation");
                                message = "Error";
                                nonce = incrementByteNonce(nonce);
                                r = CL.protect("CBC",message, nonce, key);
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                            }else {
                                // Check if the message is an error message
                                String m = decryptedJson1.get("M").getAsString();
                                if(m.equalsIgnoreCase("Error")){
                                    System.out.println("Error! Restarting conversation");
                                    //Restart Conversation
                                }else{ System.out.printf("Client received %d bytes: %s%n", len, m);}
                                // Continue to send the second message
                            }
                        }
                        // Second message
                        message = "These Bytes";
                        nonce = incrementByteNonce(nonce);
                        r = CL.protect("CBC",message, nonce, key);
                        messageBytes = r.toString().getBytes();
                        os.write(messageBytes);
                        os.flush();

                        //Listen for Response
                        // Buffer to store the total response
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        byte[] packet = new byte[16]; // Buffer for individual packets
                        int bytesRead;

                        int counter=0;
                        while ((bytesRead = is.read(packet)) != -1) {
                            buffer.write(packet, 0, bytesRead);
                            counter += bytesRead;
                            if (bytesRead < 16) {
                                // Break the loop if we receive a packet that's less than 16 bytes
                                // It indicates the end of the data stream
                                break;
                            }
                        }

                        // Convert the total response into a string
                        String totalResponse = buffer.toString();
                        JsonObject receivedJson3 = JsonParser.parseString(totalResponse).getAsJsonObject();
                        nonce = incrementByteNonce(nonce);
                        JsonObject decryptedJson3 = unprotect("CTR", receivedJson3, key, nonce);
                        if(!check(decryptedJson3.get("M").getAsString(), nonce,key,receivedJson3.get("MAC").getAsString())){
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect("CBC",message, nonce, key);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                        }else   {
                            // Check if the message is an error message
                            String m = decryptedJson3.get("M").getAsString();
                            if(m.equalsIgnoreCase("Error")){
                            System.out.println("Error! Restarting conversation");
                            //Restart Conversation
                            }else {System.out.printf("Client received %d bytes: %s%n", counter, m);}
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    } catch (JsonSyntaxException e) {
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
            }
            }

        }



    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "https_cert/user.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/usertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        String host =
               "localhost";
                //"192.168.0.100";
        int port = 8000;
        startClient(host, port);
    }
}
