package proj;

import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GUI implements ActionListener {
    private JLabel welcomeMessage = new JLabel("Hey! Let's get started");
    private JLabel musicInfo = new JLabel();
    private JFrame frame = new JFrame();
    private JButton button = new JButton("Create a User"); // Make button a class member
    private JButton chooseSongButton = new JButton("Choose Song");
    private JButton playMusicButton = new JButton("Play Music");
    private String userName; // Variable to store the user's name
    private int percentage;


    public GUI() {

        // the clickable button
        button.addActionListener(this);
        chooseSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // List of songs
                String[] songs = {"Song1", "Song2", "Song3"};
                String song = (String) JOptionPane.showInputDialog(frame,
                        "Which song would you like to listen to?",
                        "Select Song",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        songs,
                        songs[0]);

                if (song != null && !song.trim().isEmpty()) {
                    updateMessage("Hey " + userName + ", you have chosen " + song);
                    Client.getSongGUI(song);
                }
                chooseSongButton.setVisible(false);
                percentage = getPercentageInput();
                Client.getPercentageGUI(percentage);
                playMusicButton.setVisible(true);

            }
        });
        playMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle music playing logic here
                System.out.println("Playing music...");
            }
        });

        chooseSongButton.setVisible(false); // Initially hide the "Choose Song" button
        playMusicButton.setVisible(false); // Initially hide the "Play Music" button

        // the panel with the button and text
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(welcomeMessage);
        panel.add(playMusicButton);
        panel.add(button);
        panel.add(chooseSongButton);
        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GrooveGalaxy");
        frame.pack();
        frame.setVisible(true);
    }

    public void updateMessage(String message) {
        welcomeMessage.setText(message);
    }
    public void updateMessage(JsonObject mediaInfo) {
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


    // process the button clicks
    public void actionPerformed(ActionEvent e) {
        userName = JOptionPane.showInputDialog(frame, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        if (userName != null && !userName.trim().isEmpty()) {
            updateMessage("Hey " + userName); // Update message with the greeting
            button.setVisible(false); // Hide the "Create Client" button
            chooseSongButton.setVisible(true); // Show the "Choose Song" button
        }

        try {
            Client.main(new String[0]); // You might want to pass the name and song to the client
        } catch (IOException ex) {
            ex.printStackTrace(); // Handle the exception
        }
    }


    private int getPercentageInput() {
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

    // create one Frame
    public static void main(String[] args) {
        new GUI();
    }
}


