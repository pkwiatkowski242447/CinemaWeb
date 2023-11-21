package pl.pas.gr3.cinema.exceptions.repositories;

public class MovieRepositoryReadException extends MovieRepositoryException {
    public MovieRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
