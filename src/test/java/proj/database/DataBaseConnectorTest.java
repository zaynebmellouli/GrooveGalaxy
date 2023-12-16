package proj.database;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

public class DataBaseConnectorTest {

    private static final String TEST_URL = "db.url=jdbc:postgresql://localhost:5432/groovedb";
    private static final String TEST_USERNAME = "postgres";
    private static final String TEST_PASSWORD = "postgres";

    private static DataBaseConnector connector;

    @BeforeEach
     void setUp() throws DataBaseConnectionException {
        // Override properties with test values
        System.setProperty("test.db.url", TEST_URL);
        System.setProperty("test.db.username", TEST_USERNAME);
        System.setProperty("test.db.password", TEST_PASSWORD);

        // Create an instance of DataBaseConnector
        connector = new DataBaseConnector();
    }

    @AfterEach
     void tearDown() throws SQLException {
        // Close the connection after all tests
        if (connector != null) {
            connector.closeConnection();
        }
    }

    @Test
    void testCreateConnection() {
        // Ensure that the connection is created successfully
        Connection connection = connector.getConnection();
        assertNotNull(connection);
        assertDoesNotThrow(() -> connection.close());
    }

    @Test
    void testSetupTables() {
        // Mock the setup file content
        String setupContent = "-- Drop tables if they exist\n" +
                "DROP TABLE IF EXISTS media_content, media, users;\n" +
                "\n" +
                "-- Table to store users\n" +
                "CREATE TABLE users (\n" +
                "    user_id INTEGER,\n" +
                "    username VARCHAR(255) NOT NULL ,\n" +
                "    password VARCHAR(255) NOT NULL,\n" +
                "    shared_symmetric_key VARCHAR(255) NOT NULL,\n" +
                "    family_symmetric_key VARCHAR(255) NOT NULL,\n" +
                "    UNIQUE(shared_symmetric_key),\n" +
                "    UNIQUE(family_symmetric_key),\n" +
                "    PRIMARY KEY (user_id)\n" +
                ");\n" +
                "\n" +
                "-- Table to store media information\n" +
                "CREATE TABLE media (\n" +
                "    media_id INTEGER PRIMARY KEY,\n" +
                "    owner_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,\n" +
                "    format VARCHAR(50) NOT NULL,\n" +
                "    artist VARCHAR(255) NOT NULL,\n" +
                "    title VARCHAR(255) NOT NULL,\n" +
                "    genre VARCHAR(255) NOT NULL\n" +
                ");\n" +
                "\n" +
                "-- Table to store media content\n" +
                "CREATE TABLE media_content (\n" +
                "    media_id INTEGER PRIMARY KEY,\n" +
                "    FOREIGN KEY (media_id) REFERENCES media(media_id) ON DELETE CASCADE,\n" +
                "    lyrics TEXT NOT NULL,\n" +
                "    audio_base64 TEXT NOT NULL\n" +
                ");\n";
        InputStream mockInputStream = new ByteArrayInputStream(setupContent.getBytes(StandardCharsets.UTF_8));

        // Use ReflectionTestUtils to set the private field
        ReflectionTestUtils.setField(connector, "connection", connector.getConnection());
        ReflectionTestUtils.setField(connector, "classPathResource", new MockClassPathResource("dbsetup.txt", mockInputStream));

        // Ensure that setupTables executes without exceptions
        assertDoesNotThrow(() -> connector.setupTables());
    }

    // Helper class to mock ClassPathResource
    private static class MockClassPathResource extends ClassPathResource {
        private final InputStream inputStream;

        public MockClassPathResource(String path, InputStream inputStream) {
            super(path);
            this.inputStream = inputStream;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }
    }
}