package proj.database;

import proj.CL;
import proj.server_client.data_objects.User;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

public class Populate {

    private Populate() {}

    public static void populate() throws DataBaseConnectionException, SQLException, IOException {
        Connection connection = (new DataBaseConnector()).getConnection();


        User user1 = new User(1, "Alice", "abcde", Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Alice.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Lu.key")).getEncoded()));
        User user2 = new User(2, "Bob", "7894", Base64.getEncoder().encodeToString(CL.readAESKey("Keys/Key_ServClient_Bob.key").getEncoded())
                , Base64.getEncoder().encodeToString((CL.readAESKey("Keys/Key_Family_Musterman.key")).getEncoded()));
        Media media1 = new Media(1, "WAV", "Alison Chains","Man in the Box",  "Grunge");
        Media media2 = new Media(2, "FLAC", "Pink Floyd", "Breathe", "Alternative Rock");

        MediaContent mediaContent1 = new MediaContent("Man in the Box",
                "Trapped in a world," +
                        " a box of my own Container whispers, " +
                        "in this space alone Echoes of silence, " +
                        "in the walls I confide A man in the box, with nowhere to hide, " +
                        "Chained by thoughts, in a silent uproar, " +
                        "Searching for keys, to unlock the door",
                "C:\\Users\\zayne\\a51-cherilyn-zeineb-rassene\\src\\main\\resources\\songs\\Man in the box.mp3");
        MediaContent mediaContent2 = new MediaContent("Breathe",
                "Breathe, breathe in the air\n" +
                        "Don't be afraid to care\n" +
                        "Leave, but don't leave me\n" +
                        "Look around, choose your own ground\n" +
                        "Long you live and high you fly\n" +
                        "Smiles you'll give and tears you'll cry\n" +
                        "And all you touch and all you see\n" +
                        "Is all your life will ever be\n" +
                        "Run, rabbit, run\n" +
                        "Dig that hole, forget the sun\n" +
                        "When, at last, the work is done\n" +
                        "Don't sit down, it's time to dig another one\n" +
                        "Long you live and high you fly\n" +
                        "But only if you ride the tide\n" +
                        "Balanced on the biggest wave\n" +
                        "You race towards an early grave",
                "C:\\Users\\zayne\\a51-cherilyn-zeineb-rassene\\src\\main\\resources\\songs\\Breathe.mp3"
                );

        try {
            // 1. Insert data into the users table
            DatabaseUtils.addUser(connection, user1);
            DatabaseUtils.addUser(connection, user2);

            // 2. Insert data into the media table
            //DatabaseUtils.addMedia(connection, media);
            DatabaseUtils.addMedia(connection, media1);
            DatabaseUtils.addMedia(connection, media2);

            // 3. Insert data into the media_content table
            DatabaseUtils.addMediaContent(connection, mediaContent1);
            DatabaseUtils.addMediaContent(connection, mediaContent2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
