package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerActivationException extends GeneralStaffManagerException {
    public StaffManagerActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
