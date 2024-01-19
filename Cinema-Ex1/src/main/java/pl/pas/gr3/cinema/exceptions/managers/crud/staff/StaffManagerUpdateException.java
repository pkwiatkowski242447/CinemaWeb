package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerUpdateException extends GeneralStaffManagerException {
    public StaffManagerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
