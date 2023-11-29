package pl.pas.gr3.cinema.exceptions.managers;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

public class GeneralTicketManagerException extends GeneralManagerException {
    public GeneralTicketManagerException(String message) {
        super(message);
    }

    public GeneralTicketManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
