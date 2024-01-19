package pl.pas.gr3.cinema.exceptions.repositories.other.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientDeactivationException extends ClientRepositoryException {
    public ClientDeactivationException(String message) {
        super(message);
    }
}
