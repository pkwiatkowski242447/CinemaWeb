package pl.pas.gr3.cinema.exceptions.repositories;

public class MovieRepositoryCreateException extends MovieRepositoryException {
    public MovieRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
