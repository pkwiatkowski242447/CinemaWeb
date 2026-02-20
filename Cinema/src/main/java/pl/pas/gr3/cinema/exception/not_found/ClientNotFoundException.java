package pl.pas.gr3.cinema.exception.not_found;

import pl.pas.gr3.cinema.exception.general.NotFoundException;
import pl.pas.gr3.cinema.util.I18n;

public class ClientNotFoundException extends NotFoundException {

    public ClientNotFoundException() {
        super(I18n.CLIENT_NOT_FOUND);
    }

    public ClientNotFoundException(Throwable cause) {
        super(I18n.CLIENT_NOT_FOUND, cause);
    }
}
