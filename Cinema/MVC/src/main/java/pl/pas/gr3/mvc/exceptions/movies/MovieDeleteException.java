package pl.pas.gr3.mvc.exceptions.movies;

public class MovieDeleteException extends MovieOperationException {
    public MovieDeleteException(String message) {
        super(message);
    }

    public MovieDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
