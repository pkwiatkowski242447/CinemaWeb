package pl.pas.gr3.mvc.exceptions.tickets;

public class TicketOperationException extends Exception {
    public TicketOperationException(String message) {
        super(message);
    }

    public TicketOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
