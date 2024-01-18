package pl.pas.gr3.mvc.exceptions.daos.ticket;

public class TicketDaoCreateException extends GeneralTicketDaoException {
    public TicketDaoCreateException(String message) {
        super(message);
    }

    public TicketDaoCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
