package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class ClientNotActiveException extends BadRequestException {

    public ClientNotActiveException() {
        super(I18n.CLIENT_NOT_ACTIVE);
    }

    public ClientNotActiveException(Throwable cause) {
        super(I18n.CLIENT_NOT_ACTIVE, cause);
    }
}
