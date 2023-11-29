package pl.pas.gr3.cinema.exceptions.managers;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

public class GeneralMovieManagerException extends GeneralManagerException {
    public GeneralMovieManagerException(String message) {
        super(message);
    }

    public GeneralMovieManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
