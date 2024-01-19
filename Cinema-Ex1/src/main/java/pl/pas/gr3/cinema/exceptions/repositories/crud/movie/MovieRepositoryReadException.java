package pl.pas.gr3.cinema.exceptions.repositories.crud.movie;

import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;

public class MovieRepositoryReadException extends MovieRepositoryException {
    public MovieRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
