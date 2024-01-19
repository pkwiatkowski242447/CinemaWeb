package pl.pas.gr3.cinema.exceptions.repositories.other.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientActivationException extends ClientRepositoryException {
    public ClientActivationException(String message) {
        super(message);
    }
}
