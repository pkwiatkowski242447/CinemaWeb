package pl.pas.gr3.cinema.exception.repositories.crud.movie;

import pl.pas.gr3.cinema.exception.repositories.MovieRepositoryException;

public class MovieRepositoryReadException extends MovieRepositoryException {
    public MovieRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
