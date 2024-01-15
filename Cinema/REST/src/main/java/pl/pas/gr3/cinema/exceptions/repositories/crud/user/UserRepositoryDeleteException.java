package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;

public class UserRepositoryDeleteException extends UserRepositoryException {
    public UserRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
