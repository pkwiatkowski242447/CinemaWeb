package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class TicketDeleteException extends BadRequestException {

    public TicketDeleteException() {
        super(I18n.TICKET_DELETE);
    }

    public TicketDeleteException(Throwable cause) {
        super(I18n.TICKET_DELETE, cause);
    }
}
