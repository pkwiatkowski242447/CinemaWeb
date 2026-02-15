package pl.pas.gr3.cinema.exception.repositories.other.client;

import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;

public class UserDeactivationException extends UserRepositoryException {
    public UserDeactivationException(String message) {
        super(message);
    }
}
