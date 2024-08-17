package pl.pas.gr3.cinema.exceptions;

public class ApplicationInputOutputException extends ApplicationBaseException {

    public ApplicationInputOutputException() {
    }

    public ApplicationInputOutputException(String message) {
        super(message);
    }

    public ApplicationInputOutputException(Throwable cause) {
        super(cause);
    }

    public ApplicationInputOutputException(String message, Throwable cause) {
        super(message, cause);
    }
}
