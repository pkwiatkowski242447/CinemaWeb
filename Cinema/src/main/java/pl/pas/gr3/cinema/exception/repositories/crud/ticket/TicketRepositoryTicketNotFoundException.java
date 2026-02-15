package pl.pas.gr3.cinema.exception.repositories.crud.ticket;

public class TicketRepositoryTicketNotFoundException extends TicketRepositoryReadException {
    public TicketRepositoryTicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
