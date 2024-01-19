package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;

public class UserRepositoryCreateException extends UserRepositoryException {

    public UserRepositoryCreateException(String message) {
        super(message);
    }

    public UserRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
