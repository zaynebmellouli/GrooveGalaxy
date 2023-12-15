package database;

public class Queries {

    private Queries() {
    }

    /**
     * Database Queries
     */

    public static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";

    public static final String GET_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id=?";

    public static final String UPDATE_USER_QUERY = "UPDATE users SET username=?, password=?, shared_symmetric_key=?, "
            + "family_symmetric_key=?, profile_pic=? WHERE user_id=?";

    public static final String ADD_USER_QUERY = "INSERT INTO users(username, password, shared_symmetric_key, "
            + "family_symmetric_key, profile_pic) VALUES (?,?,?,?,?)";

    public static final String REMOVE_USER_QUERY = "DELETE FROM users WHERE user_id=?";

    public static final String GET_ALL_MEDIA_QUERY = "SELECT * FROM media";

    public static final String GET_MEDIA_BY_ID_QUERY = "SELECT * FROM media WHERE media_id=?";

    public static final String ADD_MEDIA_QUERY = "INSERT INTO media(owner_id, format, artist, title, genre) "
            + "VALUES (?,?,?,?,?)";

    public static final String REMOVE_MEDIA_QUERY = "DELETE FROM media WHERE media_id=?";

    public static final String GET_ALL_MEDIA_CONTENT_QUERY = "SELECT * FROM media_content";

    public static final String GET_MEDIA_CONTENT_BY_ID_QUERY = "SELECT * FROM media_content WHERE media_id=?";

    public static final String ADD_MEDIA_CONTENT_QUERY = "INSERT INTO media_content(media_id, lyrics, audio_base64) "
            + "VALUES (?,?,?)";

    public static final String REMOVE_MEDIA_CONTENT_QUERY = "DELETE FROM media_content WHERE media_id=?";
}
