package pl.pas.gr3.cinema.exceptions.services.crud.movie;

import pl.pas.gr3.cinema.exceptions.services.GeneralMovieServiceException;

public class MovieServiceUpdateException extends GeneralMovieServiceException {
    public MovieServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
