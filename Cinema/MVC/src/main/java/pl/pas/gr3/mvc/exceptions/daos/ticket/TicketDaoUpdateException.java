package pl.pas.gr3.mvc.exceptions.daos.ticket;

public class TicketDaoUpdateException extends GeneralTicketDaoException {
    public TicketDaoUpdateException(String message) {
        super(message);
    }

    public TicketDaoUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
