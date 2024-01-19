package pl.pas.gr3.cinema.exceptions.managers.crud.ticket;

import pl.pas.gr3.cinema.exceptions.managers.GeneralTicketManagerException;

public class TicketManagerCreateException extends GeneralTicketManagerException {
    public TicketManagerCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
