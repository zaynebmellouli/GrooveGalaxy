package proj;

import com.google.gson.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {
    private JLabel welcomeMessage = new JLabel("Hey! Let's get started");
    private JLabel musicInfo = new JLabel();
    private JFrame frame = new JFrame();
    private JButton createUserButton = new JButton("Create a User");
    private JButton chooseSongButton = new JButton("Choose Song");
    private JButton playMusicButton = new JButton("Play Music");
    private String userName; // Variable to store the user's name
    private String selectedSong; // Variable to store the selected song
    private int percentage; // Variable to store the percentage

    public GUI() {
        createUserButton.addActionListener(e -> promptUserName());
        chooseSongButton.addActionListener(e -> promptSongSelection());
        playMusicButton.addActionListener(e -> playMusic());

        chooseSongButton.setVisible(false); // Initially hide the "Choose Song" button
        playMusicButton.setVisible(false); // Initially hide the "Play Music" button

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(welcomeMessage);
        panel.add(musicInfo);
        panel.add(createUserButton);
        panel.add(chooseSongButton);
        panel.add(playMusicButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GrooveGalaxy");
        frame.pack();
        frame.setVisible(true);
    }

    public void promptUserName() {
        userName = JOptionPane.showInputDialog(frame, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        if (userName != null && !userName.trim().isEmpty()) {
            updateMessage("Hey " + userName);
            createUserButton.setVisible(false);
            chooseSongButton.setVisible(true);
        }
    }

    public void promptSongSelection() {
        String[] songs = {"Song1", "Song2", "Song3"};
        selectedSong = (String) JOptionPane.showInputDialog(frame,
                "Which song would you like to listen to?",
                "Select Song",
                JOptionPane.QUESTION_MESSAGE,
                null,
                songs,
                songs[0]);

        if (selectedSong != null && !selectedSong.trim().isEmpty()) {
            updateMessage("Hey " + userName + ", you have chosen " + selectedSong);
            Client.setSongGUI(selectedSong);
            Client.setPercentageGUI(getPercentageInput());
            playMusicButton.setVisible(true);
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
    public void playMusic() {
        Client.setPlay(true);
    }

    public void updateMessage(String message) {
        welcomeMessage.setText(message);
    }

    public void updateMusicInfo(JsonObject mediaInfo) {
        // Extracting information from the JsonObject and updating musicInfo label
        // ...
    }

    // Additional public methods to interact with the Client class
    // ...

    public static void main(String[] args) {
        new GUI();
    }
}
