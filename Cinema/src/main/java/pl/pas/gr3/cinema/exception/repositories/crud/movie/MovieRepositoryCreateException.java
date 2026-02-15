package pl.pas.gr3.cinema.exception.repositories.crud.movie;

import pl.pas.gr3.cinema.exception.repositories.MovieRepositoryException;

public class MovieRepositoryCreateException extends MovieRepositoryException {
    public MovieRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
