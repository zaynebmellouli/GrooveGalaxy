package proj;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static proj.CL.*;
import static proj.Server.NB_BYTES_PACKET_MUSIC;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class Client {

    public static void startClient(String host, int port) throws IOException {

        SocketFactory factory = SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {

            socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            socket.setEnabledProtocols(new String[]{"TLSv1.3"});

            String message = "This is a secure channel!";
            System.out.println("sending message: " + message);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            InputStream is   = new BufferedInputStream(socket.getInputStream());
            byte[]      data = new byte[2048];
            int         len  = is.read(data);
            System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Type: 'Exit' to close the connection");

                try {
                    SecureRandom random = new SecureRandom();
                    byte[]       nonce  = new byte[16];
                    random.nextBytes(nonce);
                    int id    = 1;
                    Key key   = CL.readAESKey("Keys/Key_ServClient_Alice.key");
                    Key key_f = CL.readAESKey("Keys/Key_Family_Lu.key");

                    //First Message
                    message = "Breathe";
                    JsonObject r            = CL.protect(message.getBytes(), nonce, id, key);
                    byte[]     messageBytes = r.toString().getBytes();
                    os = new BufferedOutputStream(socket.getOutputStream());
                    os.write(messageBytes);
                    os.flush();

                    //Listen for Response
                    is   = new BufferedInputStream(socket.getInputStream());
                    data = new byte[2048];
                    len  = is.read(data);
                    JsonObject mediaInfo            = null;
                    int        media_content_length = 0;
                    if (len != -1) {
//                            nonce        = incrementByteNonce(nonce);
                        String     firstMessage   = new String(data, 0, len);
                        JsonObject receivedJson1  = JsonParser.parseString(firstMessage).getAsJsonObject();
                        JsonObject decryptedJson1 = unprotect(receivedJson1, key, nonce);
                        if (!check( decryptedJson1.get("M").getAsString(), nonce, key, receivedJson1.get("MAC").getAsString())) {
                            System.out.println("Error! Restarting conversation");
                            message      = "Error";
                            r            = CL.protect( message.getBytes(), nonce, key);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");
                        }
                        else {
                            // Check if the message is an error message
                            String m = new String(Base64.getDecoder().decode(decryptedJson1.get("M").getAsString()));
                            if (m.equalsIgnoreCase("Error")) {
                                System.out.println("Error! Restarting conversation");
                                throw new SecurityException("...");
                                //Restart Conversation
                            }
                            else {
                                mediaInfo            = JsonParser.parseString(new String(Base64.getDecoder().decode(decryptedJson1.get("M").getAsString())))
                                                                 .getAsJsonObject();
                                media_content_length = mediaInfo.get("media_content_length").getAsInt();
                                System.out.printf("Client received %d bytes:", len);
                                System.out.println("owner_id :" + mediaInfo.get("owner_id").getAsInt());
                                System.out.println("format :" + mediaInfo.get("format").getAsString());
                                System.out.println("artist :" + mediaInfo.get("artist").getAsString());
                                System.out.println("title :" + mediaInfo.get("title").getAsString());
                                System.out.println("genre :" + mediaInfo.get("genre").getAsString());
                                System.out.println("lyrics :" + mediaInfo.get("lyrics").getAsString());
                                receiveMessage(decryptedJson1);
                            }
                            // Continue to send the second message
                        }
                    }
                    // Second message
                    do {
                        System.out.println("From which percentage do you want the music (format 0-100)?");
                        message = scanner.nextLine();
                    } while (Integer.parseInt(message) < 0 || Integer.parseInt(message) >= 100);
                    int PercentageBytes = Integer.parseInt(message);
                    message = String.valueOf(PercentageBytes);


                    nonce        = incrementByteNonce(nonce);
                    r            = CL.protect( message.getBytes(), nonce, key);
                    messageBytes = r.toString().getBytes();
                    os.write(messageBytes);
                    os.flush();


                    int from16bytes = (int) Math.floor((double) (PercentageBytes * media_content_length/100) / NB_BYTES_PACKET_MUSIC);
                    int nb16bytes   = (int) Math.ceil((double) media_content_length / NB_BYTES_PACKET_MUSIC) ;

                    Thread playerThreadCur = null;
                    Thread playerThreadPrev = null;
                    for (int i = from16bytes; i < nb16bytes; i++) {
                        //Listen for Response
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                        // Buffer to store the total response
                        is = new BufferedInputStream(socket.getInputStream());
                        byte[] packet = new byte[1000]; // Buffer for individual packets
                        byte[] mac    = new byte[64];
                        int    bytesRead;

                        while ((bytesRead = is.read(packet)) != -1) {
                            if (Arrays.equals(packet,0,bytesRead -1, "MAC".getBytes(),0,2)) {
                                is.read(mac);
                                break;
                            }else {
                                buffer.write(packet, 0, bytesRead);
                            }
//
                        }
                        // Convert the total response into a string
                        byte[] partSong = buffer.toByteArray();



                        byte[] nonceCTR = incrementCounterInNonce(nonce,i * NB_BYTES_PACKET_MUSIC);

                        byte[] decryptPartSong = unprotectCTR(partSong, key_f, nonceCTR);
                        if (!check(Base64.getEncoder().encodeToString(partSong), nonceCTR, key_f, new String(mac))) {
                            System.out.println("Error! Restarting conversation");
                            message      = "Error";
                            nonce        = incrementByteNonce(nonce);
                            r            = CL.protect(message.getBytes(), nonce, key);
                            messageBytes = r.toString().getBytes();
                            os.write(messageBytes);
                            os.flush();
                            throw new SecurityException("...");
                        }
                        else {
////                             Check if the message is an error message
//                            byte[] musicBytes =Base64.getDecoder().decode(decryptedJson3.get("M").getAsString());
//                            String m = new String(musicBytes);
//                            if (m.equalsIgnoreCase("Error")) {
//                                System.out.println("Error! Restarting conversation");
//                                throw new SecurityException("...");
//                                //Restart Conversation
//                            }
//                            else {
                                System.out.printf("Client received music from %d\n", i);
//                                    byte[] musicBytes = buffer.toByteArray();

                                try {
                                    playerThreadCur = new Thread(() -> {
                                        InputStream         in     = new ByteArrayInputStream(decryptPartSong);
                                        BufferedInputStream bis    = new BufferedInputStream(in);
                                        Player              player = null;
                                        try {
                                            player = new Player(bis);
                                        } catch (JavaLayerException e) {
                                            throw new RuntimeException(e);
                                        }
                                        try {
                                            player.play();
                                        } catch (JavaLayerException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    if (playerThreadPrev != null) {
                                        playerThreadPrev.join();
                                    }
                                    playerThreadCur.start();
                                    playerThreadPrev = playerThreadCur;
                                    System.out.println("Song part playing");
                                } catch (Exception e) {
                                    System.out.println("Problem playing the MP3 file");
                                    e.printStackTrace();
                                }
                            }
//                        }
                    }
//                        from16bytes++;
//                        } while (from16bytes < nb16bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                } catch (JsonSyntaxException e) {
                    throw new RuntimeException(e);
                } catch (SecurityException e) {
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

    private static GUI guiCallback;

    public static void setGuiCallback(GUI gui) {
        guiCallback = gui;
    }

    // Method where you receive messages
    public static void receiveMessage(JsonObject json) {
        String receivedMessage = new String(Base64.getDecoder().decode(json.get("M").getAsString()));

        if (guiCallback != null) {
            guiCallback.updateMessage(receivedMessage);
        }
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "https_cert/user.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "https_cert/usertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
        String host =
                "localhost";
//                "192.168.0.100";
        int port = 8000;
        startClient(host, port);
    }
}
