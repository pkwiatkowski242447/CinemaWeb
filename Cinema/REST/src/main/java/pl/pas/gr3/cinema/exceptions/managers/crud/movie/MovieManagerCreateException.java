package pl.pas.gr3.cinema.exceptions.managers.crud.movie;

import pl.pas.gr3.cinema.exceptions.managers.GeneralMovieManagerException;

public class MovieManagerCreateException extends GeneralMovieManagerException {
    public MovieManagerCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
