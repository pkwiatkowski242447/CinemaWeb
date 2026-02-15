package pl.pas.gr3.cinema.exception.services.crud.ticket;

import pl.pas.gr3.cinema.exception.services.GeneralTicketServiceException;

public class TicketServiceReadException extends GeneralTicketServiceException {
    public TicketServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
