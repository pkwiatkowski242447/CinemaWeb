package pl.pas.gr3.mvc.exceptions.daos.ticket;

public class TicketDaoDeleteException extends GeneralTicketDaoException {
    public TicketDaoDeleteException(String message) {
        super(message);
    }

    public TicketDaoDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
