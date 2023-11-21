package pl.pas.gr3.cinema.exceptions.repositories;

public class TicketReservationHasAlreadyEndedException extends TicketRepositoryException {
    public TicketReservationHasAlreadyEndedException(String message) {
        super(message);
    }
}
