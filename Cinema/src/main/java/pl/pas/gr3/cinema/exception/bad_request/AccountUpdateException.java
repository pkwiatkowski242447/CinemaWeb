package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class AccountUpdateException extends BadRequestException {

    public AccountUpdateException() {
        super(I18n.ACCOUNT_UPDATE);
    }

    public AccountUpdateException(Throwable cause) {
        super(I18n.ACCOUNT_UPDATE, cause);
    }
}
