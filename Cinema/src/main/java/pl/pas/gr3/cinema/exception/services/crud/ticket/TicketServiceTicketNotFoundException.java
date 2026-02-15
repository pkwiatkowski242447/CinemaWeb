package pl.pas.gr3.cinema.exception.services.crud.ticket;

public class TicketServiceTicketNotFoundException extends TicketServiceReadException {
    public TicketServiceTicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
