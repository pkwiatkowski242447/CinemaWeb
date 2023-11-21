package pl.pas.gr3.cinema.exceptions.repositories;

public class TicketRepositoryCreateException extends TicketRepositoryException {
    public TicketRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
