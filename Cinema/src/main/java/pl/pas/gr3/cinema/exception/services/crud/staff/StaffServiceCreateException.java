package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceCreateException extends GeneralStaffServiceException {

    public StaffServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
