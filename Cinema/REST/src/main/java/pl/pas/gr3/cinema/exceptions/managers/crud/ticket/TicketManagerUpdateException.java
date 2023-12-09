package pl.pas.gr3.cinema.exceptions.managers.crud.ticket;

import pl.pas.gr3.cinema.exceptions.managers.GeneralTicketManagerException;

public class TicketManagerUpdateException extends GeneralTicketManagerException {
    public TicketManagerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
