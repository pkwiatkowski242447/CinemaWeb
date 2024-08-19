package pl.pas.gr3.cinema.exceptions.authentication;

import pl.pas.gr3.cinema.utils.I18n;

public class AccountInactiveException extends ApplicationInvalidAuthenticationException {

    public AccountInactiveException() {
        super(I18n.ACCOUNT_INACTIVE_EXCEPTION);
    }

    public AccountInactiveException(String message) {
        super(message);
    }

    public AccountInactiveException(Throwable cause) {
        super(I18n.ACCOUNT_INACTIVE_EXCEPTION, cause);
    }

    public AccountInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
