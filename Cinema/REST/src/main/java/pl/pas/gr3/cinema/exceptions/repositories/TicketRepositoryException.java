package pl.pas.gr3.cinema.exceptions.repositories;

public class TicketRepositoryException extends GeneralRepositoryException {
    public TicketRepositoryException(String message) {
        super(message);
    }

    public TicketRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
