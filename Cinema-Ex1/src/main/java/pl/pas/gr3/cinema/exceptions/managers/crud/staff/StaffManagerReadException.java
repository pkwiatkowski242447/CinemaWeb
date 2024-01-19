package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerReadException extends GeneralStaffManagerException {
    public StaffManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
