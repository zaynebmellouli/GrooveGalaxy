package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private DatabaseUtils() {
    }

    /**
     * Get all users from the database
     */
    public static List<User> getAllUsers(Connection conn) throws SQLException {
        return getUsers(conn, Queries.GET_ALL_USERS_QUERY, null);
    }

    /**
     * Get a user by ID from the database
     */
    public static User getUserById(Connection conn, String userId) throws SQLException {
        List<User> users = getUsers(conn, Queries.GET_USER_BY_ID_QUERY, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Get all media from the database
     */
    public static List<Media> getAllMedia(Connection conn) throws SQLException {
        return getMedia(conn, Queries.GET_ALL_MEDIA_QUERY, null);
    }

    /**
     * Get media by ID from the database
     */
    public static Media getMediaById(Connection conn, int mediaId) throws SQLException {
        List<Media> mediaList = getMedia(conn, Queries.GET_MEDIA_BY_ID_QUERY, String.valueOf(mediaId));
        return mediaList.isEmpty() ? null : mediaList.get(0);
    }

    /**
     * Get all media content from the database
     */
    public static List<MediaContent> getAllMediaContent(Connection conn) throws SQLException {
        return getMediaContent(conn, Queries.GET_ALL_MEDIA_CONTENT_QUERY, null);
    }

    /**
     * Get media content by ID from the database
     */
    public static MediaContent getMediaContentById(Connection conn, int mediaId) throws SQLException {
        List<MediaContent> mediaContentList = getMediaContent(conn, Queries.GET_MEDIA_CONTENT_BY_ID_QUERY, String.valueOf(mediaId));
        return mediaContentList.isEmpty() ? null : mediaContentList.get(0);
    }


    public static void addUser(Connection conn, User user) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_USER_QUERY)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getSharedSymmetricKey());
            statement.setString(4, user.getFamilySymmetricKey());
            statement.setString(5, user.getProfilePic());

            statement.executeUpdate();
        }
    }

    /**
     * Add media to the database
     */
    public static void addMedia(Connection conn, Media media) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_MEDIA_QUERY)) {
            statement.setString(1, media.getOwnerId());
            statement.setString(2, media.getFormat());
            statement.setString(3, media.getArtist());
            statement.setString(4, media.getTitle());
            statement.setString(5, media.getGenre());

            statement.executeUpdate();
        }
    }

    /**
     * Add media content to the database
     */
    public static void addMediaContent(Connection conn, MediaContent mediaContent) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(Queries.ADD_MEDIA_CONTENT_QUERY)) {
            statement.setInt(1, mediaContent.getMediaId());
            statement.setString(2, mediaContent.getLyrics());
            statement.setString(3, mediaContent.getAudioBase64());

            statement.executeUpdate();
        }
    }
}