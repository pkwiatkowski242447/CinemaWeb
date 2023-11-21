package pl.pas.gr3.cinema.exceptions.repositories;

public class MovieRepositoryUpdateException extends MovieRepositoryException {
    public MovieRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
