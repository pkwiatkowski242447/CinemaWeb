package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerDeactivationException extends GeneralStaffManagerException {
    public StaffManagerDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
