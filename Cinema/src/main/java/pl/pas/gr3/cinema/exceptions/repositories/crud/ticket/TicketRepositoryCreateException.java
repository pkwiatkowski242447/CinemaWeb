package pl.pas.gr3.cinema.exceptions.repositories.crud.ticket;

import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;

public class TicketRepositoryCreateException extends TicketRepositoryException {
    public TicketRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
