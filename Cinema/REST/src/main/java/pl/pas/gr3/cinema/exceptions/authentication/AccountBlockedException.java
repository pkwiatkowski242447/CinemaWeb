package pl.pas.gr3.cinema.exceptions.authentication;

import pl.pas.gr3.cinema.utils.I18n;

public class AccountBlockedException extends ApplicationInvalidAuthenticationException {

    public AccountBlockedException() {
        super(I18n.ACCOUNT_BLOCKED_EXCEPTION);
    }

    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(Throwable cause) {
        super(I18n.ACCOUNT_BLOCKED_EXCEPTION, cause);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
