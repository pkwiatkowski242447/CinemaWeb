package pl.pas.gr3.cinema.exception.services.crud.ticket;

import pl.pas.gr3.cinema.exception.services.GeneralTicketServiceException;

public class TicketServiceDeleteException extends GeneralTicketServiceException {
    public TicketServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
