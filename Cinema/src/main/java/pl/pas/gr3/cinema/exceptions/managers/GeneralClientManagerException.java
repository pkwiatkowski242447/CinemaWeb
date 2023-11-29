package pl.pas.gr3.cinema.exceptions.managers;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

public class GeneralClientManagerException extends GeneralManagerException {
    public GeneralClientManagerException(String message) {
        super(message);
    }

    public GeneralClientManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
