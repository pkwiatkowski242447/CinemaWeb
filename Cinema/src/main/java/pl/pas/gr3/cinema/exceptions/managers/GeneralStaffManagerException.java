package pl.pas.gr3.cinema.exceptions.managers;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

public class GeneralStaffManagerException extends GeneralManagerException {
    public GeneralStaffManagerException(String message) {
        super(message);
    }

    public GeneralStaffManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
