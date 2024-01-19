package pl.pas.gr3.cinema.exceptions.repositories.other.client;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;

public class UserActivationException extends UserRepositoryException {
    public UserActivationException(String message) {
        super(message);
    }
}
