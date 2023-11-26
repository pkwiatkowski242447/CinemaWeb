package pl.pas.gr3.cinema.exceptions.repositories.crud.ticket;

import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;

public class TicketRepositoryDeleteException extends TicketRepositoryException {
    public TicketRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
