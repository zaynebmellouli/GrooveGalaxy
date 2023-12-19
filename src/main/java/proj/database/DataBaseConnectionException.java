package proj.database;


public class DataBaseConnectionException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataBaseConnectionException() {
    }

    public DataBaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}