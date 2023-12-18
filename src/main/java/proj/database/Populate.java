package proj.database;

import proj.server_client.data_objects.User;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;

import java.sql.Connection;
import java.sql.SQLException;

public class Populate {

    private Populate() {}

    public static void populate() throws DataBaseConnectionException, SQLException {
        Connection connection = (new DataBaseConnector()).getConnection();

        User user1 = new User(1,"Alice","Bob123","14634823", "462483");
        User user2 = new User(2,"Bob","Alice123","348823", "384628176");
        //Media media = new Media(1,2, "WAV1", "Harry Styles", "WaterMelon", "POP");
        Media media1 = new Media(35,1, "WAV", "Alison Chains", "Man in the Bin", "Grunge");
        Media media2 = new Media(45,2, "FLAC", "Pink Floyd", "Breathe", "Alternative Rock");

        MediaContent mediaContent1 = new MediaContent(35,
                "Trapped in a world," +
                        " a box of my own Container whispers, " +
                        "in this space alone Echoes of silence, " +
                        "in the walls I confide A man in the box, with nowhere to hide, " +
                        "Chained by thoughts, in a silent uproar, " +
                        "Searching for keys, to unlock the door",
                "C:\\Users\\zayne\\a51-cherilyn-zeineb-rassene\\src\\main\\resources\\songs\\Man in the box.mp3");
        MediaContent mediaContent2 = new MediaContent(45,
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
