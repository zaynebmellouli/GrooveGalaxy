package proj.server_client.server;

import org.json.JSONObject;
import proj.database.DatabaseUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {

    private final Connection databaseConnection;

    public DatabaseService(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Handle a request from the server to get song information by media ID and user ID.
     *
     * @param mediaId The ID of the media (song).
     * @param userId  The ID of the user.
     * @return A JSON string representing song information.
     * @throws SQLException If a database error occurs.
     */
    public JSONObject getSongInfo(int mediaId, int userId) {
        try {
            // Check if the user has ownership of the requested media
            boolean userHasAccess = DatabaseUtils.checkUserMediaOwnership(databaseConnection, userId, mediaId);

            if (userHasAccess) {
                // Get song information in JSON format
                JSONObject songInfoJson = DatabaseUtils.getSongInfoAsJsonById(databaseConnection, mediaId);
                return new JSONObject(songInfoJson);
            } else {
                // Handle case where user doesn't have access to the requested media
                return new JSONObject().put("error", "User does not have access to the requested media");
            }
        } catch (SQLException e) {
            return new JSONObject().put("error", "Database error occurred");
        } catch (Exception e) {
            return new JSONObject().put("error", "An unexpected error occurred");
        }
    }
}
