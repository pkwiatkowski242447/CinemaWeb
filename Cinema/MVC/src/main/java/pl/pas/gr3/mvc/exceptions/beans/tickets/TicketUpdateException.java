package pl.pas.gr3.mvc.exceptions.beans.tickets;

public class TicketUpdateException extends TicketOperationException {
    public TicketUpdateException(String message) {
        super(message);
    }

    public TicketUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
