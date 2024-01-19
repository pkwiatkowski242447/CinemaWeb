package pl.pas.gr3.cinema.exceptions.managers.crud.movie;

import pl.pas.gr3.cinema.exceptions.managers.GeneralMovieManagerException;

public class MovieManagerDeleteException extends GeneralMovieManagerException {
    public MovieManagerDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
