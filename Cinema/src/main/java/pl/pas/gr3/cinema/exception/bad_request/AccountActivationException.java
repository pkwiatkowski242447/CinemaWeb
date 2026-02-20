package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class AccountActivationException extends BadRequestException {

    public AccountActivationException() {
        super(I18n.ACCOUNT_ACTIVATE);
    }

    public AccountActivationException(Throwable cause) {
        super(I18n.ACCOUNT_ACTIVATE, cause);
    }
}
