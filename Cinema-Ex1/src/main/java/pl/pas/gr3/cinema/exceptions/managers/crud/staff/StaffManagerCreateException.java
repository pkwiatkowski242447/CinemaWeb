package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerCreateException extends GeneralStaffManagerException {
    public StaffManagerCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
