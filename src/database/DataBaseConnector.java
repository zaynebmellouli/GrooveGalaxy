package database;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;

import database.DatabaseConnectionException;

public class DatabaseConnector {
    private static Connection connection = null;
    private String url;
    private String username;
    private String password;
    private static final String CONFIG_FILE_NAME = "database.properties";
    private static final String DB_SETUP_FILE_NAME = "dbsetup.txt";

    public DatabaseConnector() throws DatabaseConnectionException {
        try {
            this.setProperties();
        } catch (IOException e) {
            throw new DatabaseConnectionException("Error setting properties", e);
        }
        if (connection == null) {
            try {
                this.createConnection();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
                throw new DatabaseConnectionException("Error creating connection", e);
            }
        }
    }

    private void setProperties() throws IOException {
        Properties props = new Properties();
        ClassPathResource resource = new ClassPathResource(CONFIG_FILE_NAME);
        InputStream is = resource.getInputStream();
        props.load(is);
        this.url = props.getProperty("db.url");
        this.username = props.getProperty("db.username");
        this.password = props.getProperty("db.password");
    }

    public void createConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver").newInstance();
        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    @SuppressWarnings("resource")
    public void setupTables() throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource(DB_SETUP_FILE_NAME);
        InputStream is = resource.getInputStream();
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
