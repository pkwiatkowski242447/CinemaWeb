package pl.pas.gr3.cinema.exception.repositories.other.client;

import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;

public class UserActivationException extends UserRepositoryException {
    public UserActivationException(String message) {
        super(message);
    }
}
