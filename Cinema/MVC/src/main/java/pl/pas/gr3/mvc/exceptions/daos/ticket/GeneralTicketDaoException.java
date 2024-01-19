package pl.pas.gr3.mvc.exceptions.daos.ticket;

import pl.pas.gr3.mvc.exceptions.daos.GeneralDaoException;

public class GeneralTicketDaoException extends GeneralDaoException {
    public GeneralTicketDaoException(String message) {
        super(message);
    }

    public GeneralTicketDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
