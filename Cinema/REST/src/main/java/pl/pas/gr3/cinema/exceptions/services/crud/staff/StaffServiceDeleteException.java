package pl.pas.gr3.cinema.exceptions.services.crud.staff;

import pl.pas.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceDeleteException extends GeneralStaffServiceException {
    public StaffServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
