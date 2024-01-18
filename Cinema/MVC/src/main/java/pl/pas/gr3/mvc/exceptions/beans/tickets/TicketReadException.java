package pl.pas.gr3.mvc.exceptions.beans.tickets;

public class TicketReadException extends TicketOperationException {
    public TicketReadException(String message) {
        super(message);
    }

    public TicketReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
