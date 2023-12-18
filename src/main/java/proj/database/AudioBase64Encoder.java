package proj.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class AudioBase64Encoder {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\zayne\\a51-cherilyn-zeineb-rassene\\src\\main\\resources\\songs\\Man in the box.mp3";
        try {
            // Read the audio file
            byte[] audioBytes = Files.readAllBytes(Paths.get(filePath));

            // Encode to Base64
            String audioBase64 = Base64.getEncoder().encodeToString(audioBytes);

            // Print or store the Base64 string
            System.out.println(audioBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
