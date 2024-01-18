package pl.pas.gr3.mvc.exceptions.movies;

public class MovieReadException extends MovieOperationException {
    public MovieReadException(String message) {
        super(message);
    }

    public MovieReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
