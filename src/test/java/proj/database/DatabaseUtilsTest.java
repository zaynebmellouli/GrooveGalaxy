package proj.database;

import org.junit.jupiter.api.*;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;
import proj.server_client.data_objects.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DatabaseUtilsTest {

        private static final String TEST_URL = "jdbc:your_test_database_url";
        private static final String TEST_USERNAME = "your_test_database_username";
        private static final String TEST_PASSWORD = "your_test_database_password";

        private Connection testConnection;

        @BeforeAll
        void setUp() {
            // Establish a connection to the test database
            try {
                Class.forName("org.postgresql.Driver").newInstance();
                testConnection = DriverManager.getConnection(TEST_URL, TEST_USERNAME, TEST_PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to connect to the test database.");
            }
        }

        @AfterAll
        void tearDown() {
            // Close the test database connection
            if (testConnection != null) {
                try {
                    testConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Test
        void testGetAllUsers() {
            assertDoesNotThrow(() -> {
                List<User> userList = DatabaseUtils.getAllUsers(testConnection);
                assertNotNull(userList);
                assertFalse(userList.isEmpty());
            });
        }

        @Test
        void testGetUserById() {
            assertDoesNotThrow(() -> {
                String userId = "your_test_user_id";
                User user = DatabaseUtils.getUserById(testConnection, userId);
                assertNotNull(user);
                assertEquals(userId, user.getUserId());
            });
        }

        @Test
        void testGetAllMedia() {
            assertDoesNotThrow(() -> {
                List<Media> mediaList = DatabaseUtils.getAllMedia(testConnection);
                assertNotNull(mediaList);
                assertFalse(mediaList.isEmpty());
            });
        }

        @Test
        void testGetMediaById() {
            assertDoesNotThrow(() -> {
                int mediaId = 1;  // Replace with your test media ID
                Media media = DatabaseUtils.getMediaById(testConnection, mediaId);
                assertNotNull(media);
                assertEquals(mediaId, media.getMediaId());
            });
        }

        @Test
        void testGetAllMediaContent() {
            assertDoesNotThrow(() -> {
                List<MediaContent> mediaContentList = DatabaseUtils.getAllMediaContent(testConnection);
                assertNotNull(mediaContentList);
                assertFalse(mediaContentList.isEmpty());
            });
        }

        @Test
        void testGetMediaContentById() {
            assertDoesNotThrow(() -> {
                int mediaId = 1;  // Replace with your test media ID
                MediaContent mediaContent = DatabaseUtils.getMediaContentById(testConnection, mediaId);
                assertNotNull(mediaContent);
                assertEquals(mediaId, mediaContent.getMediaId());
            });
        }

    }
