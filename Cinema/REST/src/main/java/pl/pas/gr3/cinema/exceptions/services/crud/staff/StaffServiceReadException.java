package pl.pas.gr3.cinema.exceptions.services.crud.staff;

import pl.pas.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceReadException extends GeneralStaffServiceException {
    public StaffServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
