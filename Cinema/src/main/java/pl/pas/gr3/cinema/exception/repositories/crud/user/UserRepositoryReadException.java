package pl.pas.gr3.cinema.exception.repositories.crud.user;

import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;

public class UserRepositoryReadException extends UserRepositoryException {
    public UserRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
