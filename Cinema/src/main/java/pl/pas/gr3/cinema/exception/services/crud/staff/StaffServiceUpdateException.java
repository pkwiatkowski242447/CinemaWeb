package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceUpdateException extends GeneralStaffServiceException {
    public StaffServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
