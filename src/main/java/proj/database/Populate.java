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
        Media media = new Media(1,2, "WAV1", "Harry Styles", "WaterMelon", "POP");
        Media media1 = new Media(35,1, "WAV", "Alison Chains", "Man in the Bin", "Grunge");
        Media media2 = new Media(45,2, "FLAC", "Soundgarden", "Black Hole Sun", "Alternative Rock");

        MediaContent mediaContent1 = new MediaContent(35,
                "Trapped in a world," +
                        " a box of my own Container whispers, " +
                        "in this space alone Echoes of silence, " +
                        "in the walls I confide A man in the box, with nowhere to hide, " +
                        "Chained by thoughts, in a silent uproar, " +
                        "Searching for keys, to unlock the door",
                "UklGRiQIAAAWQVZAAWQVZFZm10IBAAAAABAAFZm10IBAAAAAAAWQVZFZm10IBAAAWQVZFZm10IBAAAAABAAAAAABAABAAIAEABAAWQVZFZm10IBAAAAABAAHACAAA...ABGAAQAFABkYXRhAAgA==");

        MediaContent mediaContent2 = new MediaContent(45,
                "In my eyes, indisposed" +
                        " In disguises no one knows" +
                        " Hides the face, " +
                        "lies the snake The sun in my disgrace",
                "R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZuqJcO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2LfO2Ll+WvWM+aO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2ffO2f//2CH5BAkAIAAhAAAAAAQAAEAAAESBDISWw2MQpGGhxMlMAlRQKWnhwhR0Z6CgQKGN5eEGFomSIpJABVVFxYWcggwIVZSFRRgU6EBECAA0TUwMKC1pLCAkAIfkEAQAAEAAsAAAAABAAEAAABH7QgIE6YpAMhFBkSo0VrGYwESYQA7");

        try {
            // 1. Insert data into the users table
            DatabaseUtils.addUser(connection, user1);
            DatabaseUtils.addUser(connection, user2);

            // 2. Insert data into the media table
            DatabaseUtils.addMedia(connection, media);
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
