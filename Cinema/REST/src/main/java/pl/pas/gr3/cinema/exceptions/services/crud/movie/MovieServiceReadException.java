package pl.pas.gr3.cinema.exceptions.services.crud.movie;

import pl.pas.gr3.cinema.exceptions.services.GeneralMovieServiceException;

public class MovieServiceReadException extends GeneralMovieServiceException {
    public MovieServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
