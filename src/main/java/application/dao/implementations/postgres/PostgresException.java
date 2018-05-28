package application.dao.implementations.postgres;

public class PostgresException extends Exception {

    public PostgresException() {
    }

    public PostgresException(String message) {
        super(message);
    }

    public PostgresException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostgresException(Throwable cause) {
        super(cause);
    }

    public PostgresException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
