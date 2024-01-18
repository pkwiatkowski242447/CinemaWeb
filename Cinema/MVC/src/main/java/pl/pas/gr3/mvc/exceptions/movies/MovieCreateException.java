package pl.pas.gr3.mvc.exceptions.movies;

public class MovieCreateException extends MovieOperationException {
    public MovieCreateException(String message) {
        super(message);
    }

    public MovieCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
