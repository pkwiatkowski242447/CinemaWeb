package pl.pas.gr3.cinema.exceptions.services.crud.movie;

import pl.pas.gr3.cinema.exceptions.services.GeneralMovieServiceException;

public class MovieServiceDeleteException extends GeneralMovieServiceException {
    public MovieServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
