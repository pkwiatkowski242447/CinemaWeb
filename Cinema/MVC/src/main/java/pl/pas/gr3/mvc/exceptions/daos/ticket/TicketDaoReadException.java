package pl.pas.gr3.mvc.exceptions.daos.ticket;

public class TicketDaoReadException extends GeneralTicketDaoException {
    public TicketDaoReadException(String message) {
        super(message);
    }

    public TicketDaoReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
