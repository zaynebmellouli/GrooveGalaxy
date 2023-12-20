package proj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GUI implements ActionListener {
    private JLabel messageLabel = new JLabel("Hey! Let's get started");
    private JFrame frame = new JFrame();
    private JButton button = new JButton("Create a User"); // Make button a class member
    private JButton chooseSongButton = new JButton("Choose Song");
    private String userName; // Variable to store the user's name


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
                }
                chooseSongButton.setVisible(false);
            }
        });

        chooseSongButton.setVisible(false); // Initially hide the "Choose Song" button

        // the panel with the button and text
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(messageLabel);
        panel.add(button);
        panel.add(chooseSongButton);
        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI");
        frame.pack();
        frame.setVisible(true);
    }

    public void updateMessage(String message) {
        messageLabel.setText(message);
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



    // create one Frame
    public static void main(String[] args) {
        new GUI();
    }
}


