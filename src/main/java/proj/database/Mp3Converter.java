package proj.database;

import javazoom.jl.player.Player;
import org.apache.commons.io.FileUtils;


import java.io.*;


public class Mp3Converter {

    public static byte[] readAudioFile(String filePath) throws IOException {
        File audioFile = new File(filePath);
        return FileUtils.readFileToByteArray(audioFile);
    }

    public static void main(String[] args) {

            try {
                String filePath = "src/main/resources/songs/Man in the box.mp3";
                byte[] musicBytes = readAudioFile(filePath);
                InputStream         in     = new ByteArrayInputStream(musicBytes);
                BufferedInputStream bis    = new BufferedInputStream(in);
                Player              player = new Player(bis);
                player.play();
            } catch (Exception e) {
                System.out.println("Problem playing the MP3 file");
                e.printStackTrace();
            }
        }
    }
