package proj.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;

public class DataBaseConnector {

    private Connection connection = null;
    private final String url;
    private final String username;
    private final String password;
    private static final String DB_SETUP_FILE_NAME = "dbsetup.txt";
    private ClassPathResource classPathResource;
    
    

    public DataBaseConnector() throws DataBaseConnectionException {
//        this.url = "jdbc:postgresql://localhost:5432/groovedb"; // for test others
//        this.url = "jdbc:postgresql://localhost:5433/groovedb"; // For test rassene
        this.url = "jdbc:postgresql://192.168.0.1:5432/groovedb"; // for submit
        this.username = "postgres";
        this.password = "postgres";

            try {
                this.createConnection();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
                throw new DataBaseConnectionException("Error creating connection", e);
            }

    }

    public void createConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    public void setupTables() throws IOException, SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            throw new SQLException("Connection is closed");
        }
        // Ensure that classPathResource is not null before using it
        if (classPathResource == null) {
            throw new IllegalStateException("classPathResource is not initialized.");
        }

        InputStream is = classPathResource.getInputStream();
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String queryStatement = s.hasNext() ? s.next() : "";
        try (Statement statement = this.connection.createStatement()) {
            statement.execute(queryStatement);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }
}
