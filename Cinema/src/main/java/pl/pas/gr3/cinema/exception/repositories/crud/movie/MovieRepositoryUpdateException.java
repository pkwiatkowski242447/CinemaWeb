package pl.pas.gr3.cinema.exception.repositories.crud.movie;

import pl.pas.gr3.cinema.exception.repositories.MovieRepositoryException;

public class MovieRepositoryUpdateException extends MovieRepositoryException {
    public MovieRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
