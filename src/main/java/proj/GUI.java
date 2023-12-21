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

    }

    public static void main(String[] args) {
        new GUI();
    }
}
