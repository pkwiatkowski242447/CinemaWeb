package pl.pas.gr3.mvc.exceptions.beans.movies;

public class MovieUpdateException extends MovieOperationException {
    public MovieUpdateException(String message) {
        super(message);
    }

    public MovieUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
