package pl.pas.gr3.cinema.exceptions.managers.crud.movie;

import pl.pas.gr3.cinema.exceptions.managers.GeneralMovieManagerException;

public class MovieManagerReadException extends GeneralMovieManagerException {
    public MovieManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
