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
import java.nio.charset.StandardCharsets;
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

    public static final int NB_BYTES_PACKET_MUSIC = 500000;

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
                        String song = new String(Base64.getDecoder().decode(decryptedJson1.get("M").getAsString()));
                        if(!check(decryptedJson1.get("M").getAsString(),id,nonce, keyServClient,receivedJson1.get("MAC").getAsString())){
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
                        BufferedOutputStream bufferedOutput = new BufferedOutputStream(os, 1000);
                        bufferedOutput.write(messageBytes);
                        bufferedOutput.flush();
                        bufferedOutput.write("stop".getBytes());
                        bufferedOutput.flush();

                        //Listen for Response - Percentage
                        len = is.read(data);
                        if (len == -1) {
                            System.out.println("Error! Restarting conversation");
                            message = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect(message.getBytes(), nonce, keyFamily);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");}
                        message = new String(data, 0, len);
                        JsonObject receivedJson2 = JsonParser.parseString(message).getAsJsonObject();
                        nonce = incrementByteNonce(nonce);
                        //decrypt the message
                        int byteReqInt = 0;
                        JsonObject decryptedJson2 = CL.unprotect( receivedJson2, keyServClient, nonce);
                        String byteReq = new String(Base64.getDecoder().decode(decryptedJson2.get("M").getAsString()));
                        if(!check(decryptedJson2.get("M").getAsString(),nonce, keyServClient,receivedJson2.get("MAC").getAsString())){
                            System.out.println("Integrity check failed. Sending error message to client.");
                            // Send error message to server
                            String errorMessage = "Error";
                            nonce = incrementByteNonce(nonce);
                            r = CL.protect(errorMessage.getBytes(), nonce, keyFamily);
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
                                r = CL.protect(errorMessage.getBytes(), nonce, keyFamily);
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                throw new SecurityException("...");
                            } else {
                                byteReqInt = Integer.parseInt(byteReq);
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
                        byte[] encryptedOriginalSong = CL.protectCTR(originalSong, nonce, keyFamily);
                        int from16bytes = (int) Math.floor((byteReqInt/ 100.0) * encryptedOriginalSong.length  / NB_BYTES_PACKET_MUSIC);
                        int nb16bytes = (int) Math.ceil((double) encryptedOriginalSong.length / NB_BYTES_PACKET_MUSIC) ;


                        for (int i = from16bytes; i < nb16bytes; i++) {
                            byte[] partEncryptedSong = givePartByte(encryptedOriginalSong, i);
                            byte[] nonceCTR = incrementCounterInNonce(nonce,i * NB_BYTES_PACKET_MUSIC);
//                            BufferedOutputStream bufferedOutput = new BufferedOutputStream(os, 1000);
                            bufferedOutput = new BufferedOutputStream(os, 1000);
                            bufferedOutput.write(partEncryptedSong);
                            bufferedOutput.flush();
                            bufferedOutput.write("MAC".getBytes());
                            bufferedOutput.flush();
                            bufferedOutput.write(calculateMAC(Base64.getEncoder().encodeToString(partEncryptedSong),nonceCTR, keyFamily).getBytes());
                            bufferedOutput.flush();
//                            os = new BufferedOutputStream(socket.getOutputStream());
//                            os.write(encryptedBytes);


                            System.out.println("Sending song part");
//                            bufferedOutput.close();

//                        String parSong = new String(encryptedBytes, StandardCharsets.UTF_8);
//
//
//                        JsonObject receivedJson3 = JsonParser.parseString(parSong).getAsJsonObject();


//                        JsonObject decryptedJson3 = unprotect("CBC", receivedJson3, keyFamily, nonce);
//                        byte[] musicBytes =  unprotectCTR(partEncryptedSong, keyFamily, nonceCTR);
//                        byte[] musicBytesu =  givePartByte(originalSong, i);

                        //                        boolean val = check(decryptedJson3.get("M").getAsString(),nonce, keyFamily,receivedJson3.get("MAC").getAsString());
//                                    byte[] musicBytes = buffer.toByteArray();

//                        try {
//                            InputStream         in     = new ByteArrayInputStream(musicBytes);
//                            BufferedInputStream bis    = new BufferedInputStream(in);
//                            Player              player = new Player(bis);
//                            player.play();
//                            while (!player.isComplete()) {
//                            }
//                            System.out.println("Song part finished playing");
//                        } catch (Exception e) {
//                            System.out.println("Problem playing the MP3 file");
//                            e.printStackTrace();
//                        }
                        }



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

    public static byte[] givePartByte(byte[] originalSong, int nb) {
        int baselength = NB_BYTES_PACKET_MUSIC;
        int newLength = baselength;
        if ((nb * baselength) +baselength >= originalSong.length) {
            newLength =  originalSong.length - nb * newLength;// Or handle this case as you see fit
        }
        // Create a new array to hold the result
        byte[] result = new byte[newLength];

        // Copy the relevant part of the original array into the result
        System.arraycopy(originalSong, nb*baselength, result, 0, newLength);
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
