package pl.pas.gr3.cinema.exception.services.crud.movie;

import pl.pas.gr3.cinema.exception.services.GeneralMovieServiceException;

public class MovieServiceUpdateException extends GeneralMovieServiceException {
    public MovieServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
