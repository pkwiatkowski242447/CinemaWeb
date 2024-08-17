package pl.pas.gr3.cinema.exceptions.account;

import pl.pas.gr3.cinema.exceptions.ApplicationBaseException;

public class AccountBaseException extends ApplicationBaseException {

    public AccountBaseException() {
    }

    public AccountBaseException(String message) {
        super(message);
    }

    public AccountBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountBaseException(Throwable cause) {
        super(cause);
    }
}
