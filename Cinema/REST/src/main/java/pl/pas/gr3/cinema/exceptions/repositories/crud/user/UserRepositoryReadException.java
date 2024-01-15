package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;

public class UserRepositoryReadException extends UserRepositoryException {
    public UserRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
