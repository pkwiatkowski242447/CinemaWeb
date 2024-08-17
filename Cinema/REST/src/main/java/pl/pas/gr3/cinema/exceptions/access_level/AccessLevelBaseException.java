package pl.pas.gr3.cinema.exceptions.access_level;

import pl.pas.gr3.cinema.exceptions.ApplicationBaseException;

public class AccessLevelBaseException extends ApplicationBaseException {

    public AccessLevelBaseException() {
    }

    public AccessLevelBaseException(String message) {
        super(message);
    }

    public AccessLevelBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessLevelBaseException(Throwable cause) {
        super(cause);
    }
}
