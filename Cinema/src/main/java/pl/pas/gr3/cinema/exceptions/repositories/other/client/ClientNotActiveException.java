package pl.pas.gr3.cinema.exceptions.repositories.other.client;

import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;

public class ClientNotActiveException extends TicketRepositoryException {
    public ClientNotActiveException(String message) {
        super(message);
    }
}
