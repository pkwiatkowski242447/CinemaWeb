package pl.pas.gr3.mvc.exceptions.tickets;

public class TicketCreateException extends TicketOperationException {
    public TicketCreateException(String message) {
        super(message);
    }

    public TicketCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
