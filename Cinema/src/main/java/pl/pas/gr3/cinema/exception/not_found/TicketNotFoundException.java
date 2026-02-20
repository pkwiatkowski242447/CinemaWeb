package pl.pas.gr3.cinema.exception.not_found;

import pl.pas.gr3.cinema.exception.general.NotFoundException;
import pl.pas.gr3.cinema.util.I18n;

public class TicketNotFoundException extends NotFoundException {

    public TicketNotFoundException() {
        super(I18n.TICKET_NOT_FOUND);
    }

    public TicketNotFoundException(Throwable cause) {
        super(I18n.TICKET_NOT_FOUND, cause);
    }
}
