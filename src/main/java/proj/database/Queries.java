package proj.database;

public class Queries {

    private Queries() {
    }

    /**
     * Database Queries
     */

    public static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";

    public static final String GET_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id=?";

    public static final String UPDATE_USER_QUERY = "UPDATE users SET username=?, password=?, shared_symmetric_key=?, "
            + "family_symmetric_key=? WHERE user_id=?";

    public static final String ADD_USER_QUERY = "INSERT INTO users (user_id, username, password, shared_symmetric_key, family_symmetric_key) VALUES (?, ?, ?, ?, ?);";

    public static final String REMOVE_USER_QUERY = "DELETE FROM users WHERE user_id=?";

    public static final String GET_ALL_MEDIA_QUERY = "SELECT * FROM media";

    public static final String GET_MEDIA_BY_ID_QUERY = "SELECT * FROM media WHERE media_id=?";

    public static final String ADD_MEDIA_QUERY = "INSERT INTO media(media_id, owner_id, format, artist, title, genre) "
            + "VALUES (?,?,?,?,?,?)";

    public static final String REMOVE_MEDIA_QUERY = "DELETE FROM media WHERE media_id=?";

    public static final String GET_ALL_MEDIA_CONTENT_QUERY = "SELECT * FROM media_content";

    public static final String GET_MEDIA_CONTENT_BY_ID_QUERY = "SELECT * FROM media_content WHERE media_id=?";

    public static final String ADD_MEDIA_CONTENT_QUERY = "INSERT INTO media_content(media_id, file_path, lyrics) "
            + "VALUES (?,?,?)";

    public static final String COUNT_MEDIA_BY_ID = "SELECT COUNT(*) AS count FROM media WHERE owner_id = ? AND media_id = ?";
}
