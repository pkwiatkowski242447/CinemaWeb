package pl.pas.gr3.cinema.exceptions.repositories.crud.ticket;

public class TicketRepositoryTicketNotFoundException extends TicketRepositoryReadException {
    public TicketRepositoryTicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
