package pl.pas.gr3.mvc.exceptions.beans.tickets;

public class TicketDeleteException extends TicketOperationException {
    public TicketDeleteException(String message) {
        super(message);
    }

    public TicketDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
