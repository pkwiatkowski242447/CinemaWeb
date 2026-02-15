package pl.pas.gr3.cinema.exception.repositories.crud.ticket;

import pl.pas.gr3.cinema.exception.repositories.TicketRepositoryException;

public class TicketRepositoryReadException extends TicketRepositoryException {
    public TicketRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
