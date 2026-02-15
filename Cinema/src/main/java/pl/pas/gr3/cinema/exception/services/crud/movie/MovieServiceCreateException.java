package pl.pas.gr3.cinema.exception.services.crud.movie;

import pl.pas.gr3.cinema.exception.services.GeneralMovieServiceException;

public class MovieServiceCreateException extends GeneralMovieServiceException {
    public MovieServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
