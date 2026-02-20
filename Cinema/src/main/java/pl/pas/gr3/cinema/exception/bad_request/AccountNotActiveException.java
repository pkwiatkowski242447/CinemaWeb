package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class AccountNotActiveException extends BadRequestException {

    public AccountNotActiveException() {
        super(I18n.ACCOUNT_NOT_ACTIVE);
    }

    public AccountNotActiveException(String key, Throwable cause) {
        super(I18n.ACCOUNT_NOT_ACTIVE, cause);
    }
}
