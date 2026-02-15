package pl.pas.gr3.cinema.exception.services.crud.movie;

import pl.pas.gr3.cinema.exception.services.GeneralMovieServiceException;

public class MovieServiceDeleteException extends GeneralMovieServiceException {
    public MovieServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
