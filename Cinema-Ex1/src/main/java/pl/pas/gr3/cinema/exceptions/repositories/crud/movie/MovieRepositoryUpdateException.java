package pl.pas.gr3.cinema.exceptions.repositories.crud.movie;

import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;

public class MovieRepositoryUpdateException extends MovieRepositoryException {
    public MovieRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
