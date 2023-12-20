package proj;

import com.google.gson.*;

import java.io.*;
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
import com.google.gson.JsonObject;
import javax.swing.*;
import java.awt.*;

public class Client {
    //GUI Global Variables
    private static JLabel welcomeMessage = new JLabel("Hey! Let's get started");
    private static JLabel musicInfo = new JLabel();
    private static JFrame frame = new JFrame();
    private static JButton createUserButton = new JButton("Create a User");
    private static JButton chooseSongButton = new JButton("Choose Song");
    private static JButton playMusicButton = new JButton("Play Music");

    //Global Variables
    private static String userName; // Variable to store the user's name
    private static int percentageBytes = Integer.MAX_VALUE;
    private static String choosenSong = null;
    private static InputStream is;
    private static int len;
    private static byte[] nonce;
    private static int id;
    private static Key key_c;
    private static Key key_f;
    private static SSLSocket socket;
    private static BufferedOutputStream os;
    private static byte[] data;
    private static int media_content_length;


    public static void startClient(String host, int port) throws IOException {

        SocketFactory factory = SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(host, port);
        try{

            socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            socket.setEnabledProtocols(new String[]{"TLSv1.3"});

            String message = "This is a secure channel!";
            System.out.println("sending message: " + message);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(message.getBytes());
            os.flush();

            is   = new BufferedInputStream(socket.getInputStream());
            data = new byte[2048];
            len  = is.read(data);
            System.out.printf("client received %d bytes: %s%n", len, new String(data, 0, len));

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Type: 'Exit' to close the connection");

                try {
                    SecureRandom random = new SecureRandom();
                    byte[]       nonce  = new byte[16];
                    random.nextBytes(nonce);
                    id    = 1;
                    key_c = CL.readAESKey("Keys/Key_ServClient_Alice.key");
                    key_f = CL.readAESKey("Keys/Key_Family_Lu.key");
                    createUserButton.addActionListener(e -> promptUserName());
                    chooseSongButton.addActionListener(e -> {
                        try {
                            promptSongSelection();
                        } catch (GeneralSecurityException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    playMusicButton.addActionListener(e -> {
                        try {
                            playMusic();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (GeneralSecurityException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                    chooseSongButton.setVisible(false); // Initially hide the "Choose Song" button
                    playMusicButton.setVisible(false); // Initially hide the "Play Music" button

                    JPanel panel = new JPanel();
                    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
                    panel.setLayout(new GridLayout(0, 1));
                    //panel.setPreferredSize(new Dimension(width, height));
                    panel.add(welcomeMessage);
                    panel.add(musicInfo);
                    panel.add(createUserButton);
                    panel.add(chooseSongButton);
                    panel.add(playMusicButton);

                    frame.add(panel, BorderLayout.CENTER);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    //frame.setSize(width, height)
                    // frame.pack() //to fit the contents
                    frame.setTitle("GrooveGalaxy");
                    frame.pack();
                    frame.setVisible(true);

                } catch (IOException e) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    public static void promptUserName() {
        userName = JOptionPane.showInputDialog(frame, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        if (userName != null && !userName.trim().isEmpty()) {
            updateMessage("Hey " + userName);
            createUserButton.setVisible(false);
            chooseSongButton.setVisible(true);
        }
    }

    public static void promptSongSelection() throws GeneralSecurityException, IOException {
        String[] songs = {"Song1", "Song2", "Song3"};
        choosenSong = (String) JOptionPane.showInputDialog(frame,
                "Which song would you like to listen to?",
                "Select Song",
                JOptionPane.QUESTION_MESSAGE,
                null,
                songs,
                songs[0]);

        if (choosenSong != null && !choosenSong.trim().isEmpty()) {
            updateMessage("Hey " + userName + ", you have chosen " + choosenSong);
            //First Message
            //message = "Breathe";
            JsonObject r            = CL.protect(choosenSong.getBytes(), nonce, id, key_c);
            byte[]     messageBytes = r.toString().getBytes();
            os = new BufferedOutputStream(socket.getOutputStream());
            os.write(messageBytes);
            os.flush();
            //Listen for Response
            is   = new BufferedInputStream(socket.getInputStream());
            data = new byte[2048];
            len  = is.read(data);
            JsonObject mediaInfo            = null;
            if (len != -1) {
//                            nonce        = incrementByteNonce(nonce);
                String     firstMessage   = new String(data, 0, len);
                JsonObject receivedJson1  = JsonParser.parseString(firstMessage).getAsJsonObject();
                JsonObject decryptedJson1 = unprotect(receivedJson1, key_c, nonce);
                if (!check( decryptedJson1.get("M").getAsString(), nonce, key_c, receivedJson1.get("MAC").getAsString())) {
                    System.out.println("Error! Restarting conversation");
                    String message      = "Error";
                    r            = CL.protect( message.getBytes(), nonce, key_c);
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
                        //System.out.printf("Client received %d bytes:", len);
                        //System.out.println("owner_id :" + mediaInfo.get("owner_id").getAsInt());
                        //System.out.println("format :" + mediaInfo.get("format").getAsString());
                        //System.out.println("artist :" + mediaInfo.get("artist").getAsString());
                        //System.out.println("title :" + mediaInfo.get("title").getAsString());
                        //System.out.println("genre :" + mediaInfo.get("genre").getAsString());
                        //System.out.println("lyrics :" + mediaInfo.get("lyrics").getAsString());
                        updateMusicInfo(mediaInfo);
                    }
                    // Continue to send the second message
                }
            }
            percentageBytes = getPercentageInput();
            String message = String.valueOf(percentageBytes);
            nonce        = incrementByteNonce(nonce);
            r            = CL.protect( message.getBytes(), nonce, key_c);
            messageBytes = r.toString().getBytes();
            os.write(messageBytes);
            os.flush();
            playMusicButton.setVisible(true);
        }
    }


    public static void playMusic() throws IOException, GeneralSecurityException {
        int from16bytes = (int) Math.floor((double) (percentageBytes * media_content_length/100) / NB_BYTES_PACKET_MUSIC);
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
                String message      = "Error";
                nonce = incrementByteNonce(nonce);
                JsonObject r = CL.protect(message.getBytes(), nonce, key_c);
                byte[] messageBytes = r.toString().getBytes();
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

    }

    private static int getPercentageInput() {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, "Enter the starting point for streaming (0-100%):", "Percentage", JOptionPane.PLAIN_MESSAGE);
            try {
                int percentage = Integer.parseInt(input);
                if (percentage >= 0 && percentage <= 100) {
                    return percentage; // Valid input, return the percentage
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid percentage (0-100).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void updateMessage(String message) {
        welcomeMessage.setText(message);
    }


    public static void updateMusicInfo(JsonObject mediaInfo) {
        // Extracting information from the JsonObject
        int mediaContentLength = mediaInfo.get("media_content_length").getAsInt();
        int ownerId = mediaInfo.get("owner_id").getAsInt();
        String format = mediaInfo.get("format").getAsString();
        String artist = mediaInfo.get("artist").getAsString();
        String title = mediaInfo.get("title").getAsString();
        String genre = mediaInfo.get("genre").getAsString();
        String lyrics = mediaInfo.get("lyrics").getAsString();

        // Formatting the message
        String message = "<html>Media Content Length: " + mediaContentLength +
                "<br>Owner ID: " + ownerId +
                "<br>Format: " + format +
                "<br>Artist: " + artist +
                "<br>Title: " + title +
                "<br>Genre: " + genre +
                "<br>Lyrics: " + lyrics + "</html>";

        // Updating the label
        musicInfo.setText(message);
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
