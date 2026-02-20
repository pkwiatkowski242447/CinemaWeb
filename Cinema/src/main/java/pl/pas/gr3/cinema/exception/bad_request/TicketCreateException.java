package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class TicketCreateException extends BadRequestException {

    public TicketCreateException() {
        super(I18n.TICKET_CREATE);
    }

    public TicketCreateException(Throwable cause) {
        super(I18n.TICKET_CREATE, cause);
    }
}
