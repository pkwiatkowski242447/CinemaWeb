package pl.pas.gr3.cinema.exceptions.repositories.other.client;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;

public class UserDeactivationException extends UserRepositoryException {
    public UserDeactivationException(String message) {
        super(message);
    }
}
