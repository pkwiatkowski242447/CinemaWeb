package pl.pas.gr3.cinema.exception.repositories.crud.ticket;

import pl.pas.gr3.cinema.exception.repositories.TicketRepositoryException;

public class TicketRepositoryUpdateException extends TicketRepositoryException {
    public TicketRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
