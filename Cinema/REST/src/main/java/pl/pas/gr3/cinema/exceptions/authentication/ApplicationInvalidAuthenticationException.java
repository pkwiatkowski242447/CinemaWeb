package pl.pas.gr3.cinema.exceptions.authentication;

import pl.pas.gr3.cinema.exceptions.ApplicationBaseException;
import pl.pas.gr3.cinema.utils.I18n;

public class ApplicationInvalidAuthenticationException extends ApplicationBaseException {

    public ApplicationInvalidAuthenticationException() {
        super(I18n.APPLICATION_INVALID_AUTHENTICATION_EXCEPTION);
    }

    public ApplicationInvalidAuthenticationException(String message) {
        super(message);
    }

    public ApplicationInvalidAuthenticationException(Throwable cause) {
        super(I18n.APPLICATION_INVALID_AUTHENTICATION_EXCEPTION, cause);
    }

    public ApplicationInvalidAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
