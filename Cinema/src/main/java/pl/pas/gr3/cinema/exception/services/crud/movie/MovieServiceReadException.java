package pl.pas.gr3.cinema.exception.services.crud.movie;

import pl.pas.gr3.cinema.exception.services.GeneralMovieServiceException;

public class MovieServiceReadException extends GeneralMovieServiceException {
    public MovieServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
