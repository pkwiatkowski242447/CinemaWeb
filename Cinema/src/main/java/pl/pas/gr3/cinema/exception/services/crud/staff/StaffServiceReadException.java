package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceReadException extends GeneralStaffServiceException {
    public StaffServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
