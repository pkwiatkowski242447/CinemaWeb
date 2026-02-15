package pl.pas.gr3.cinema.exception.services.crud.ticket;

import pl.pas.gr3.cinema.exception.services.GeneralTicketServiceException;

public class TicketServiceUpdateException extends GeneralTicketServiceException {
    public TicketServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
