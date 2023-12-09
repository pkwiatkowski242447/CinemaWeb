package pl.pas.gr3.cinema.exceptions.managers.crud.ticket;

import pl.pas.gr3.cinema.exceptions.managers.GeneralTicketManagerException;

public class TicketManagerReadException extends GeneralTicketManagerException {
    public TicketManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
