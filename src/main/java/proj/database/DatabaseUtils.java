package proj.database;


import proj.server_client.data_objects.MediaInfo;
import proj.server_client.data_objects.User;
import proj.server_client.data_objects.Media;
import proj.server_client.data_objects.MediaContent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    /**
     * Get users from the database based on a specific query
     */
    private static List<User> getUsers(Connection conn, String query, int parameter) throws SQLException {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            // If a parameter is provided, set it in the prepared statement
            if (parameter != 0) {
                statement.setInt(1, parameter);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = extractUserFromResultSet(resultSet);
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    /**
     * Extract User object from the ResultSet
     */
    private static User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setSharedSymmetricKey(resultSet.getString("shared_symmetric_key"));
        user.setFamilySymmetricKey(resultSet.getString("family_symmetric_key"));
        return user;
    }


    /**
     * Get a user by ID from the database
     */
    public static User getUserById(Connection conn, int userId) throws SQLException {
        List<User> users = getUsers(conn, Queries.GET_USER_BY_ID_QUERY, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Get media from the database based on a specific query
     */
    private static List<Media> getMedia(Connection conn, String query, String parameter) throws SQLException {
        List<Media> mediaList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Media media = extractMediaFromResultSet(resultSet);
                    mediaList.add(media);
                }
            }
        }
        return mediaList;
    }

    /**
     * Extract Media object from the ResultSet
     */
    private static Media extractMediaFromResultSet(ResultSet resultSet) throws SQLException {
        Media media = new Media();
        media.setTitle(resultSet.getString("title"));
        media.setOwnerId(resultSet.getInt("owner_id"));
        media.setFormat(resultSet.getString("format"));
        media.setArtist(resultSet.getString("artist"));
        media.setGenre(resultSet.getString("genre"));
        return media;
    }

    /**
     * Get media by ID from the database
     */
    public static Media getMediaByTitle(Connection conn, String title) throws SQLException {
        List<Media> mediaList = getMedia(conn, Queries.GET_MEDIA_BY_TITLE_QUERY, title);
        return mediaList.isEmpty() ? null : mediaList.get(0);
    }
    /**
     * Get media content from the database based on a specific query
     */
    private static List<MediaContent> getMediaContent(Connection conn, String query, String parameter) throws SQLException {
        List<MediaContent> mediaContentList = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, parameter);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    MediaContent mediaContent = extractMediaContentFromResultSet(resultSet);
                    mediaContentList.add(mediaContent);
                }
            }
        }
        return mediaContentList;
    }

    /**
     * Extract MediaContent object from the ResultSet
     */
    private static MediaContent extractMediaContentFromResultSet(ResultSet resultSet) throws SQLException {
        MediaContent mediaContent = new MediaContent();
        mediaContent.setTitleContent(resultSet.getString("title_content"));
        mediaContent.setLyrics(resultSet.getString("lyrics"));
        mediaContent.setFilePath(resultSet.getString("file_path"));
        //mediaContent.setAudiobase64(resultSet.getString("audio_base64"));
        return mediaContent;
    }

    /**
     * Get media content by ID from the database
     */
    public static MediaContent getMediaContentById(Connection conn, String title) throws SQLException {
        List<MediaContent> mediaContentList = getMediaContent(conn, Queries.GET_MEDIA_CONTENT_BY_TITLE_QUERY, title);
        return mediaContentList.isEmpty() ? null : mediaContentList.get(0);
    }


    public static void addUser(Connection conn, User user) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, user.getUserId()); // Assuming user.getUserId() returns a String
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getSharedSymmetricKey());
            statement.setString(5, user.getFamilySymmetricKey());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding user failed, no rows affected.");
            }
        }
    }

    public static void addMedia(Connection conn, Media media) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_MEDIA_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, media.getTitle());
            statement.setInt(2, media.getOwnerId());
            statement.setString(3, media.getFormat());
            statement.setString(4, media.getArtist());
            statement.setString(5, media.getGenre());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding media failed, no rows affected.");
            }
        }
    }

    public static void addMediaContent(Connection conn, MediaContent mediaContent) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_MEDIA_CONTENT_QUERY)) {
            statement.setString(1, mediaContent.getTitleContent());
            statement.setString(2, mediaContent.getLyrics());
            statement.setString(3, mediaContent.getFilePath());
            //statement.setString(4, mediaContent.getAudiobase64());

            statement.executeUpdate();
        }
    }

    public static JsonObject getSongInfoAsJsonById(Connection conn, String title) throws SQLException {
        Media media = getMediaByTitle(conn, title);
        MediaContent mediaContent = getMediaContentById(conn, title);

        return MediaInfo.toJson(media, mediaContent);
    }

    public static boolean checkUserMediaOwnership(Connection conn, int userId, String title) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.COUNT_MEDIA_BY_ID)) {
            statement.setInt(1, userId);
            statement.setString(2, title);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static String getUserKeyById(Connection conn, int userId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.GET_USER_KEY_BY_ID_QUERY)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("shared_symmetric_key");
                }
            }
        }
        return null;
    }

    public static String getFamilyKeyById(Connection conn, int userId) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.GET_FAMILY_KEY_BY_ID_QUERY)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("family_symmetric_key");
                }
            }
        }
        return null;
    }

    public static JsonObject getSongInfo(Connection conn, String title, int userId) throws SQLException, UserAccessException {
        // Check if the user has ownership of the requested media
        boolean userHasAccess = DatabaseUtils.checkUserMediaOwnership(conn, userId, title);

        if (userHasAccess) {
            // Get song information in JSON format
            JsonObject songInfoJson = DatabaseUtils.getSongInfoAsJsonById(conn, title);
            return songInfoJson;
        } else {
            // Handle case where user doesn't have access to the requested media
            throw new UserAccessException("User does not have access to the requested media.");
        }
    }

}