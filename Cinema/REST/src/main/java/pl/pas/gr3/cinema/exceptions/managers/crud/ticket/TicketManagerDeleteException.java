package pl.pas.gr3.cinema.exceptions.managers.crud.ticket;

import pl.pas.gr3.cinema.exceptions.managers.GeneralTicketManagerException;

public class TicketManagerDeleteException extends GeneralTicketManagerException {
    public TicketManagerDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
