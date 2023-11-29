package pl.pas.gr3.cinema.exceptions.managers.crud.staff;

import pl.pas.gr3.cinema.exceptions.managers.GeneralStaffManagerException;

public class StaffManagerDeleteException extends GeneralStaffManagerException {
    public StaffManagerDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
