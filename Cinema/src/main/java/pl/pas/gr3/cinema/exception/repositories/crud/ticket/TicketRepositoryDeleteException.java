package pl.pas.gr3.cinema.exception.repositories.crud.ticket;

import pl.pas.gr3.cinema.exception.repositories.TicketRepositoryException;

public class TicketRepositoryDeleteException extends TicketRepositoryException {
    public TicketRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
