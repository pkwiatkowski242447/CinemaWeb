package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceDeleteException extends GeneralStaffServiceException {
    public StaffServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
