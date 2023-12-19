package proj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javazoom.jl.player.Player;
import proj.database.DataBaseConnectionException;
import proj.database.DataBaseConnector;
import proj.database.DatabaseUtils;
import proj.database.UserAccessException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import static proj.CL.*;

public class Server {

    public static void startServer(int port) throws IOException {
        ServerSocketFactory factory    = SSLServerSocketFactory.getDefault();
        try (SSLServerSocket listener = (SSLServerSocket) factory.createServerSocket(port)) {

            listener.setNeedClientAuth(true);
            listener.setEnabledCipherSuites(new String[] { "TLS_AES_128_GCM_SHA256" });
            listener.setEnabledProtocols(new String[] { "TLSv1.3" });
            System.out.println("listening for messages...");
            String message = "";
            InputStream is = null;
            OutputStream os = null;
            int id = 0;
            byte[] nonce = new byte[16];

            try (Socket socket = listener.accept();
                 Connection connection = (new DataBaseConnector()).getConnection();) {
                Key keyServClient = null;
                Key keyFamily     = null;

                while (!message.equals("Exit")) {
                    try {
                        is = new BufferedInputStream(socket.getInputStream());
                        byte[] data = new byte[2048];
                        int len = is.read(data);
                        if (len == -1) {
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient, keyFamily);
                            byte[] messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");}
                        message = new String(data, 0, len);
                        os = new BufferedOutputStream(socket.getOutputStream());
                        System.out.printf("server received %d bytes: %s%n", len, message);
                        String response = message + " processed by server";
                        os.write(response.getBytes(), 0, response.getBytes().length);
                        os.flush();


                        //first message
                        len = is.read(data);
                        if (len == -1) {
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient, keyFamily);
                            byte[] messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");}
                        message = new String(data, 0, len);
                        JsonObject receivedJson1 = JsonParser.parseString(message).getAsJsonObject();
                        //get the key of the client from the id form the json
                         id = receivedJson1.get("ID").getAsInt();
                        keyServClient = new SecretKeySpec(Base64.getDecoder().decode(DatabaseUtils.getUserKeyById(connection, id)),"AES");
                        keyFamily     = new SecretKeySpec(Base64.getDecoder().decode(DatabaseUtils.getFamilyKeyById(connection, id)),"AES");

                        nonce = Base64.getDecoder().decode(receivedJson1.get("Nonce").getAsString());
                         //to change the key to the key in the database
                        //decrypt the message
                        JsonObject decryptedJson1 = CL.unprotect(receivedJson1, keyServClient);
                        String song = decryptedJson1.get("M").getAsString();
                        if(!check(song,id,nonce, keyServClient,receivedJson1.get("MAC").getAsString())){
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient, keyFamily);
                            byte[] messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");
                            }else {
                                // Check if the message is an error message
                                if (song.equalsIgnoreCase("Error")) {
                                    System.out.println("Error message received. Aborting communication.");
                                    throw new SecurityException("...");
                                } else {
                                    System.out.printf("Server received %d bytes: %s%n", len, song);
                                    // Continue to send the second message
                                }
                            }



                        //Contact Database for the Song Info
                        //Check for Song in the Database

                        //Respond to first message
                        JsonObject songInfo = DatabaseUtils.getSongInfo(connection,song, id).getAsJsonObject();
                        JsonObject media = JsonParser.parseString(songInfo.get("media").getAsString()).getAsJsonObject();
                        JsonObject media_content = JsonParser.parseString(songInfo.get("media_content").getAsString()).getAsJsonObject();
                        // Read the audio file
                        byte[] audioBytes = Files.readAllBytes(Paths.get(media_content.get("file_path").getAsString()));

                        // Encode to Base64
                        String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);
                        media_content.addProperty("file_Bytes",audioBase64);

                        media.addProperty("media_content_length",audioBytes.length);
                        media.addProperty("lyrics",media_content.get("lyrics").getAsString());
                        message = media.toString();

                        //Sending of Song info
                        JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient, keyFamily);
                        byte[] messageBytes = r.toString().getBytes();
                        os = new BufferedOutputStream(socket.getOutputStream());
                        os.write(messageBytes);
                        os.flush();

                        //Listen for Response - Percentage
                        len = is.read(data);
                        if (len == -1) {
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect("CTR",message.getBytes(), nonce, keyFamily);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");}
                        message = new String(data, 0, len);
                        JsonObject receivedJson2 = JsonParser.parseString(message).getAsJsonObject();
                        nonce = incrementByteNonce(nonce);
                        //decrypt the message
                        int byteReqInt = 0;
                        JsonObject decryptedJson2 = CL.unprotect("CBC", receivedJson2, keyServClient, nonce);
                        String byteReq = decryptedJson2.get("M").getAsString();
                        if(!check(byteReq,nonce, keyServClient,receivedJson2.get("MAC").getAsString())){
                            System.out.println("Integrity check failed. Sending error message to client.");
                            // Send error message to server
                            String errorMessage = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect("CTR",errorMessage.getBytes(), nonce, keyFamily);
                            //HELP - the client is expecting a concatinated message with the family fey
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");

                        }else {
                            // Check if the message is an error message
                            if (byteReq.equalsIgnoreCase("error")) {
                                System.out.println("Error message received. Aborting communication.");
                                // Send error message to server
                                String errorMessage = "Error";
                                nonce = incrementByteNonce(nonce);
                                r = CL.protect("CTR",errorMessage.getBytes(), nonce, keyFamily);
                                //HELP - the client is expecting a concatinated message with the family fey
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                throw new SecurityException("...");
                            } else {
                                byteReqInt = decryptedJson2.get("M").getAsInt();
                                System.out.printf("Server received %d bytes: He want the music form the %d percentage", len, byteReqInt);
                                // Continue to send the second message
                            }
                        }

                        //Respond to second message
//                        byte[] originalSong = Base64.getDecoder().decode(media_content.get("file_Bytes").getAsString());
//                        byte[] cutsongBytes= dropFirstXPercentBits(originalSong, byteReqInt);
//                        String cutSong = Base64.getEncoder().encodeToString(cutsongBytes);
//                        nonce = incrementByteNonce(nonce);
//                        JsonObject encryptedResponse = CL.protect("CTR", cutSong.getBytes(), nonce, keyFamily);
//                        byte[] encryptedBytes = encryptedResponse.toString().getBytes();
//                        os.write(encryptedBytes);
//                        os.flush();
                        byte[] originalSong = Base64.getDecoder().decode(media_content.get("file_Bytes").getAsString());
                        int from16bytes = (int) Math.floor(byteReqInt * originalSong.length / 16.0);
                        int nb16bytes = (int) Math.ceil(originalSong.length / 16.0) - from16bytes;

//                        do{
//                            for (int i = 0; i < 15; i++) {


                                byte[]     adjustedNonce     = incrementCounterInNonce(nonce, from16bytes);
                                byte[]     partSong          = gives16thBytes(originalSong, from16bytes);
                                JsonObject encryptedResponse = CL.protect("CTR", partSong, adjustedNonce, keyFamily);
                                byte[]     encryptedBytes    = encryptedResponse.toString().getBytes();
//                            try {
//                                InputStream in = new ByteArrayInputStream(partSong);
//                                BufferedInputStream bis = new BufferedInputStream(in);
//                                Player player = new Player(bis);
//                                player.play();
//                                while (!player.isComplete()) {
//                                }
//                            }
//                            catch (Exception e) {
//                                System.out.println("Problem playing the MP3 file");
//                                e.printStackTrace();
//                            }
                                os.write(encryptedBytes);
                                os.write(encryptedBytes, 0, encryptedBytes.length);
                                os.flush();
                                from16bytes++;
//                            }
//                        } while (from16bytes < nb16bytes);


                        //int offset = 0;
                        //while (offset < encryptedBytes.length) {
                        //    int chunkSize = Math.min(16, encryptedBytes.length - offset);
                        //    os.write(encryptedBytes, offset, chunkSize);
                        //    os.flush();
                        //    offset += chunkSize;
                        //}


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
                    } catch (UserAccessException e) {
                        throw new RuntimeException(e);
                    }catch (SecurityException e) {
                        startServer(port);
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
            } catch (DataBaseConnectionException e){
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] dropFirstXPercentBits(byte[] originalSong, int percentToDrop) {
        int totalLength = originalSong.length;
        int bytesToDrop = (int) Math.ceil(totalLength * (percentToDrop / 100.0));

        if (bytesToDrop >= totalLength) {
            return new byte[0]; // Or handle this case as you see fit
        }
        // Create a new array to hold the result
        int newLength = totalLength - bytesToDrop;
        byte[] result = new byte[newLength];

        // Copy the relevant part of the original array into the result
        System.arraycopy(originalSong, bytesToDrop, result, 0, newLength);
        return result;
    }

    public static byte[] gives16thBytes(byte[] originalSong, int nb) {
        int newLength = 1000;
        if ((nb * newLength) +16 >= originalSong.length) {
            newLength = nb * newLength - originalSong.length;// Or handle this case as you see fit
        }
        // Create a new array to hold the result
        byte[] result = new byte[newLength];

        // Copy the relevant part of the original array into the result
        System.arraycopy(originalSong, nb*16, result, 0, newLength);
        return result;
    }

    public static void main(String args[]) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "https_cert/server.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/servertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        int port = 8000;
        startServer(port);
    }
}
