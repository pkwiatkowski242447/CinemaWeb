package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class AccountCreateException extends BadRequestException {

    public AccountCreateException() {
        super(I18n.ACCOUNT_CREATE);
    }

    public AccountCreateException(Throwable cause) {
        super(I18n.ACCOUNT_CREATE, cause);
    }
}
