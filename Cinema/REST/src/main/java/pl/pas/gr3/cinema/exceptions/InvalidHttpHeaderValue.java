package pl.pas.gr3.cinema.exceptions;

import pl.pas.gr3.cinema.utils.I18n;

public class InvalidHttpHeaderValue extends ApplicationBaseException {

    public InvalidHttpHeaderValue() {
        super(I18n.APPLICATION_INVALID_HTTP_HEADER_VALUE_EXCEPTION);
    }

    public InvalidHttpHeaderValue(String message) {
        super(message);
    }

    public InvalidHttpHeaderValue(Throwable cause) {
        super(I18n.APPLICATION_INVALID_HTTP_HEADER_VALUE_EXCEPTION, cause);
    }

    public InvalidHttpHeaderValue(String message, Throwable cause) {
        super(message, cause);
    }
}
