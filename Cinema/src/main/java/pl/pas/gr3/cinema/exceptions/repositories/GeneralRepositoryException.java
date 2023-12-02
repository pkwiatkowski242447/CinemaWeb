package pl.pas.gr3.cinema.exceptions.repositories;

public class GeneralRepositoryException extends Exception {
    public GeneralRepositoryException(String message) {
        super(message);
    }

    public GeneralRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
