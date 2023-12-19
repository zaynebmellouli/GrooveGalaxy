package proj.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;
import proj.server_client.data_objects.User;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseUtilsTest {

    private static DataBaseConnector connector;
    private static Connection connection;

    @BeforeAll
    static void setUpAll() throws DataBaseConnectionException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        connector = new DataBaseConnector();
        connector.createConnection();
        connection = connector.getConnection();
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        if (connector != null) {
            connector.closeConnection();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Clear tables or perform any setup needed before each test
        clearTables();
    }

    @Test
    void testAddUserAndGetUserById() throws SQLException {
        User user = new User();
        user.setUserId(12);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setSharedSymmetricKey("sharedKey");
        user.setFamilySymmetricKey("familyKey");

        DatabaseUtils.addUser(connection, user);

        User retrievedUser = DatabaseUtils.getUserById(connection, user.getUserId());

        assertNotNull(retrievedUser);
        assertEquals(user.getUsername(), retrievedUser.getUsername());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
        assertEquals(user.getSharedSymmetricKey(), retrievedUser.getSharedSymmetricKey());
        assertEquals(user.getFamilySymmetricKey(), retrievedUser.getFamilySymmetricKey());
    }

//    @Test
//    void testAddMediaAndGetMediaById() throws SQLException {
//        Media media = new Media();
//        media.setOwnerId(2);
//        media.setFormat("mp3");
//        media.setArtist("testArtist");
//        media.setTitle("testTitle");
//        media.setGenre("testGenre");
//
//        DatabaseUtils.addMedia(connection, media);
//
//        Media retrievedMedia = DatabaseUtils.getMediaById(connection, media.getMediaId());
//
//        assertNotNull(retrievedMedia);
//        assertEquals(media.getOwnerId(), retrievedMedia.getOwnerId());
//        assertEquals(media.getFormat(), retrievedMedia.getFormat());
//        assertEquals(media.getArtist(), retrievedMedia.getArtist());
//        assertEquals(media.getTitle(), retrievedMedia.getTitle());
//        assertEquals(media.getGenre(), retrievedMedia.getGenre());
//    }

//    @Test
//    void testAddMediaContentAndGetMediaContentById() throws SQLException {
//        MediaContent mediaContent = new MediaContent();
//        mediaContent.setMediaId(1);
//        mediaContent.setLyrics("testLyrics");
//        mediaContent.setAudioBase64("testAudioBase64");
//
//        DatabaseUtils.addMediaContent(connection, mediaContent);
//
//        MediaContent retrievedMediaContent = DatabaseUtils.getMediaContentById(connection, mediaContent.getMediaId());
//
//        assertNotNull(retrievedMediaContent);
//        assertEquals(mediaContent.getMediaId(), retrievedMediaContent.getMediaId());
//        assertEquals(mediaContent.getLyrics(), retrievedMediaContent.getLyrics());
//        assertEquals(mediaContent.getAudioBase64(), retrievedMediaContent.getAudioBase64());
//    }

    private void clearTables() throws SQLException {
        // Implement logic to clear tables or perform any setup needed before each test
        // You may use DELETE statements to remove data from tables
    }
}
