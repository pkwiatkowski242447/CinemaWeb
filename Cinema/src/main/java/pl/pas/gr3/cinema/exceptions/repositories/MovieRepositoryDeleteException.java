package pl.pas.gr3.cinema.exceptions.repositories;

public class MovieRepositoryDeleteException extends MovieRepositoryException {
    public MovieRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
