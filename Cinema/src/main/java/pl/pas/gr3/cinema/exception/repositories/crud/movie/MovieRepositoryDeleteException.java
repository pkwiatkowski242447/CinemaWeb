package pl.pas.gr3.cinema.exception.repositories.crud.movie;

import pl.pas.gr3.cinema.exception.repositories.MovieRepositoryException;

public class MovieRepositoryDeleteException extends MovieRepositoryException {
    public MovieRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
