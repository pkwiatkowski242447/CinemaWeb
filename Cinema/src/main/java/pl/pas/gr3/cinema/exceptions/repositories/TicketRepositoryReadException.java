package pl.pas.gr3.cinema.exceptions.repositories;

public class TicketRepositoryReadException extends TicketRepositoryException {
    public TicketRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
