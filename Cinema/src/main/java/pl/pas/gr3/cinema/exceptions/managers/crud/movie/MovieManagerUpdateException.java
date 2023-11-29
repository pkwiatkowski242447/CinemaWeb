package pl.pas.gr3.cinema.exceptions.managers.crud.movie;

import pl.pas.gr3.cinema.exceptions.managers.GeneralMovieManagerException;

public class MovieManagerUpdateException extends GeneralMovieManagerException {
    public MovieManagerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
