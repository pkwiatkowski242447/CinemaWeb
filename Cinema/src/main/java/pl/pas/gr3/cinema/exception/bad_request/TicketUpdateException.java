package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class TicketUpdateException extends BadRequestException {

    public TicketUpdateException() {
        super(I18n.TICKET_UPDATE);
    }

    public TicketUpdateException(Throwable cause) {
        super(I18n.TICKET_UPDATE, cause);
    }
}
