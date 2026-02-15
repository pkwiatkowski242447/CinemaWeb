package pl.pas.gr3.cinema.exception.repositories.other.client;

import pl.pas.gr3.cinema.exception.repositories.TicketRepositoryException;

public class ClientNotActiveException extends TicketRepositoryException {
    public ClientNotActiveException(String message) {
        super(message);
    }
}
