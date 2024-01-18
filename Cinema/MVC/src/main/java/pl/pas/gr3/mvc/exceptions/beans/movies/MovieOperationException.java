package pl.pas.gr3.mvc.exceptions.beans.movies;

public class MovieOperationException extends Exception {
    public MovieOperationException(String message) {
        super(message);
    }

    public MovieOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
