package pl.pas.gr3.cinema.exception.services.crud.ticket;

import pl.pas.gr3.cinema.exception.services.GeneralTicketServiceException;

public class TicketServiceCreateException extends GeneralTicketServiceException {
    public TicketServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
