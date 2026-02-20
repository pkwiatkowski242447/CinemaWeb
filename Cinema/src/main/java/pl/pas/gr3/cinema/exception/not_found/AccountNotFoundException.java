package pl.pas.gr3.cinema.exception.not_found;

import pl.pas.gr3.cinema.exception.general.NotFoundException;
import pl.pas.gr3.cinema.util.I18n;

public class AccountNotFoundException extends NotFoundException {

    public AccountNotFoundException() {
        super(I18n.ACCOUNT_NOT_FOUND);
    }

    public AccountNotFoundException(Throwable cause) {
        super(I18n.ACCOUNT_NOT_FOUND, cause);
    }
}
