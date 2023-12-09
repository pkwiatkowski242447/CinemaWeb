package pl.pas.gr3.cinema.exceptions.repositories.crud.movie;

import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;

public class MovieRepositoryCreateException extends MovieRepositoryException {
    public MovieRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
