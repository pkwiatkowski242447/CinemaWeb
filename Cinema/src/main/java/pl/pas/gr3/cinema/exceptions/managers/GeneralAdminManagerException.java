package pl.pas.gr3.cinema.exceptions.managers;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

public class GeneralAdminManagerException extends GeneralManagerException {
    public GeneralAdminManagerException(String message) {
        super(message);
    }

    public GeneralAdminManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
