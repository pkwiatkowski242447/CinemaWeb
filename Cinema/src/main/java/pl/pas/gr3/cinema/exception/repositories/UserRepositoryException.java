package pl.pas.gr3.cinema.exception.repositories;

public class UserRepositoryException extends GeneralRepositoryException {
    public UserRepositoryException(String message) {
        super(message);
    }

    public UserRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
