package pl.pas.gr3.cinema.exceptions;

public class ApplicationBaseException extends RuntimeException {

    public ApplicationBaseException() {
    }

    public ApplicationBaseException(String message) {
        super(message);
    }

    public ApplicationBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationBaseException(Throwable cause) {
        super(cause);
    }
}
