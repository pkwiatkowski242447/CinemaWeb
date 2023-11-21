package pl.pas.gr3.cinema.exceptions.repositories;

public class ClientNotActiveException extends TicketRepositoryException {
    public ClientNotActiveException(String message) {
        super(message);
    }
}
