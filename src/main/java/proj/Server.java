package proj;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import proj.database.DataBaseConnectionException;
import proj.database.DataBaseConnector;
import proj.database.DatabaseUtils;
import proj.database.UserAccessException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import static proj.CL.*;


public class Server {

    public static final int NB_BYTES_PACKET_MUSIC = 500000;

    public static void startServer(int port) throws IOException {
        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        try (SSLServerSocket listener = (SSLServerSocket) factory.createServerSocket(port)) {

            listener.setNeedClientAuth(true);
            listener.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            listener.setEnabledProtocols(new String[]{"TLSv1.3"});
            System.out.println("listening for messages...");
            String       message = "";
            InputStream  is      = null;
            OutputStream os      = null;
            int          id      = 0;
            byte[]       nonce   = new byte[16];

            while (!message.equals("Exit")) {
                try (Socket socket = listener.accept();
                     Connection connection = (new DataBaseConnector()).getConnection();) {
                    Key keyServClient = null;
                    Key keyFamily     = null;

                    try {
                        is = new BufferedInputStream(socket.getInputStream());
                        byte[] data = new byte[2048];
                        int    len  = is.read(data);
                        if (len != -1) {
                            message = new String(data, 0, len);
                            os      = new BufferedOutputStream(socket.getOutputStream());
                            System.out.printf("server received %d bytes: %s%n", len, message);
                            String response = message + " processed by server";
                            os.write(response.getBytes(), 0, response.getBytes().length);
                            os.flush();
                        }
                        //first message
                        len = is.read(data);
                        String song = "";
                        if (len != -1) {
                            message = new String(data, 0, len);
                            JsonObject receivedJson1 = JsonParser.parseString(message).getAsJsonObject();
                            System.out.println("Received encrypted message: " + receivedJson1.toString());
                            //get the key of the client from the id form the json
                            id            = receivedJson1.get("ID").getAsInt();
                            keyServClient = new SecretKeySpec(
                                    Base64.getDecoder().decode(DatabaseUtils.getUserKeyById(connection, id)),
                                    "AES");
                            keyFamily     = new SecretKeySpec(
                                    Base64.getDecoder().decode(DatabaseUtils.getFamilyKeyById(connection, id)),
                                    "AES");

                            nonce = Base64.getDecoder().decode(receivedJson1.get("Nonce").getAsString());
                            //to change the key to the key in the database
                            //decrypt the message
                            JsonObject decryptedJson1 = CL.unprotect(receivedJson1, keyServClient);
                            System.out.println("Decrypted message: " + decryptedJson1.toString());
                            song = new String(
                                    Base64.getDecoder().decode(decryptedJson1.get("M").getAsString()));
                            if (!check(decryptedJson1.get("M").getAsString(), id, nonce, keyServClient,
                                       receivedJson1.get("MAC").getAsString())) {
                                System.out.println("Error! Restarting conversation");
                                message = "Error";
                                nonce   = incrementByteNonce(nonce);
                                JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient,
                                                          keyFamily);
                                byte[] messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                throw new SecurityException("...");
                            }
                            else {
                                // Check if the message is an error message
                                if (song.equalsIgnoreCase("Error")) {
                                    System.out.println("Error message received. Aborting communication.");
                                    throw new SecurityException("...");
                                }
                                else {
                                    System.out.printf("Server received %d bytes: %s%n", len, song);
                                    // Continue to send the second message
                                }
                            }
                        }
                        //Respond to first message
                        JsonObject songInfo = DatabaseUtils.getSongInfo(connection, song, id)
                                                           .getAsJsonObject();
                        JsonObject media = JsonParser.parseString(songInfo.get("media").getAsString())
                                                     .getAsJsonObject();
                        JsonObject media_content = JsonParser.parseString(
                                songInfo.get("media_content").getAsString()).getAsJsonObject();
                        // Read the audio file
                        byte[] audioBytes = Files.readAllBytes(
                                Paths.get(media_content.get("file_path").getAsString()));

                        // Encode to Base64
                        String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);
                        media_content.addProperty("file_Bytes", audioBase64);

                        media.addProperty("media_content_length", audioBytes.length);
                        media.addProperty("lyrics", media_content.get("lyrics").getAsString());
                        message = media.toString();
                        System.out.println("Unprotect song info: " + message);
                        //Sending of Song info
                        JsonObject r = CL.protect(message.getBytes(), nonce, keyServClient,
                                                  keyFamily);
                        System.out.println("Sending encrypted song info: " + r.toString());
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
                            message      = "Error";
                            nonce        = incrementByteNonce(nonce);
                            r            = CL.protect(message.getBytes(), nonce, keyFamily);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");
                        }
                        message = new String(data, 0, len);
                        JsonObject receivedJson2 = JsonParser.parseString(message).getAsJsonObject();
                        nonce = incrementByteNonce(nonce);
                        //decrypt the message
                        int        byteReqInt     = 0;
                        System.out.println("Received encrypted message: " + receivedJson2.toString());
                        JsonObject decryptedJson2 = CL.unprotect(receivedJson2, keyServClient, nonce);
                        System.out.println("Decrypted message: " + decryptedJson2.toString());
                        String byteReq = new String(
                                Base64.getDecoder().decode(decryptedJson2.get("M").getAsString()));
                        if (!check(decryptedJson2.get("M").getAsString(), nonce, keyServClient,
                                   receivedJson2.get("MAC").getAsString())) {
                            System.out.println("Integrity check failed. Sending error message to client.");
                            // Send error message to server
                            String errorMessage = "Error";
                            nonce = incrementByteNonce(nonce);
                            r     = CL.protect(errorMessage.getBytes(), nonce, keyFamily);
                            //HELP - the client is expecting a concatinated message with the family fey
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");

                        }
                        else {
                            // Check if the message is an error message
                            if (byteReq.equalsIgnoreCase("error")) {
                                System.out.println("Error message received. Aborting communication.");
                                // Send error message to server
                                String errorMessage = "Error";
                                nonce        = incrementByteNonce(nonce);
                                r            = CL.protect(errorMessage.getBytes(), nonce, keyFamily);
                                messageBytes = r.toString().getBytes();
                                os.write(messageBytes);
                                os.flush();
                                throw new SecurityException("...");
                            }
                            else {
                                byteReqInt = Integer.parseInt(byteReq);
                                System.out.printf(
                                        "Server received %d bytes: He want the music form the %d percentage",
                                        len, byteReqInt);
                                // Continue to send the second message
                            }
                        }

                        //Respond to second message

                        byte[] originalSong = Base64.getDecoder()
                                                    .decode(media_content.get("file_Bytes")
                                                                         .getAsString());
                        System.out.println("Protecting song" + originalSong.toString());
                        byte[] encryptedOriginalSong = CL.protectCTR(originalSong, nonce, keyFamily);
                        System.out.println("Protected song" + encryptedOriginalSong.toString());

                        int from16bytes = (int) Math.floor(
                                (byteReqInt / 100.0) * encryptedOriginalSong.length / NB_BYTES_PACKET_MUSIC);
                        int nb16bytes = (int) Math.ceil(
                                (double) encryptedOriginalSong.length / NB_BYTES_PACKET_MUSIC);


                        for (int i = from16bytes; i < nb16bytes; i++) {
                            byte[] partEncryptedSong = givePartByte(encryptedOriginalSong, i);
                            byte[] nonceCTR = incrementCounterInNonce(nonce,
                                                                      i * NB_BYTES_PACKET_MUSIC);
                            bufferedOutput = new BufferedOutputStream(os, 1000);
                            System.out.println("Sending part encrypted song"+partEncryptedSong);
                            bufferedOutput.write(partEncryptedSong);
                            bufferedOutput.flush();
                            System.out.println("Mac");
                            bufferedOutput.write("MAC".getBytes());
                            bufferedOutput.flush();
                            byte[] mac = calculateMAC(Base64.getEncoder().encodeToString(partEncryptedSong),
                                                      nonceCTR, keyFamily).getBytes();
                            System.out.println("Sending mac"+mac);
                            bufferedOutput.write(mac);
                            bufferedOutput.flush();


                            System.out.println("Sending song part");
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    } catch (UserAccessException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        is.close();
                        os.close();
                        socket.close();
                    } catch (IOException i) {
                        System.out.println(i);
                        return;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (DataBaseConnectionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static byte[] givePartByte(byte[] originalSong, int nb) {
        int baselength = NB_BYTES_PACKET_MUSIC;
        int newLength  = baselength;
        if ((nb * baselength) + baselength >= originalSong.length) {
            newLength = originalSong.length - nb * newLength;// Or handle this case as you see fit
        }
        // Create a new array to hold the result
        byte[] result = new byte[newLength];

        // Copy the relevant part of the original array into the result
        System.arraycopy(originalSong, nb * baselength, result, 0, newLength);
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
