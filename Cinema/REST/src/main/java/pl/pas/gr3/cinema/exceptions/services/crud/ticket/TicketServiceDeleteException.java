package pl.pas.gr3.cinema.exceptions.services.crud.ticket;

import pl.pas.gr3.cinema.exceptions.services.GeneralTicketServiceException;

public class TicketServiceDeleteException extends GeneralTicketServiceException {
    public TicketServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
