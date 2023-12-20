package proj.database;

public class Queries {

    private Queries() {
    }

    /**
     * Database Queries
     */

    public static final String GET_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id=?";

    public static final String ADD_USER_QUERY = "INSERT INTO users (user_id, username, password, shared_symmetric_key, family_symmetric_key) VALUES (?, ?, ?, ?, ?);";

    public static final String GET_MEDIA_BY_TITLE_QUERY = "SELECT * FROM media WHERE title=?";

    public static final String ADD_MEDIA_QUERY = "INSERT INTO media(media_id, title, owner_id, format, artist, genre) "
            + "VALUES (?,?,?,?,?,?)";

    public static final String GET_MEDIA_CONTENT_BY_TITLE_QUERY = "SELECT * FROM media_content WHERE title_content =?";

    public static final String ADD_MEDIA_CONTENT_QUERY = "INSERT INTO media_content(media_content_id, title_content,owner_id, lyrics, file_path) "
            + "VALUES (?,?,?,?,?)";

    public static final String GET_FAMILY_KEY_BY_ID_QUERY = "SELECT family_symmetric_key FROM users WHERE user_id = ?";

    public static final String GET_USER_KEY_BY_ID_QUERY = "SELECT shared_symmetric_key FROM users WHERE user_id = ?";

    public static final String COUNT_MEDIA_BY_ID = "SELECT COUNT(*) AS count FROM media WHERE owner_id = ? AND title = ?";
}
