package pl.pas.gr3.cinema.exceptions.repositories.crud.movie;

import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;

public class MovieRepositoryDeleteException extends MovieRepositoryException {
    public MovieRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
