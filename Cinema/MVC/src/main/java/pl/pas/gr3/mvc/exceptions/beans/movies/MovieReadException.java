package pl.pas.gr3.mvc.exceptions.beans.movies;

public class MovieReadException extends MovieOperationException {
    public MovieReadException(String message) {
        super(message);
    }

    public MovieReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
