package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceDeactivationException extends GeneralStaffServiceException {
    public StaffServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
