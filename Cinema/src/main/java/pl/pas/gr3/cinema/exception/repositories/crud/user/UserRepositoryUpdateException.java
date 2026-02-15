package pl.pas.gr3.cinema.exception.repositories.crud.user;

import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;

public class UserRepositoryUpdateException extends UserRepositoryException {
    public UserRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
