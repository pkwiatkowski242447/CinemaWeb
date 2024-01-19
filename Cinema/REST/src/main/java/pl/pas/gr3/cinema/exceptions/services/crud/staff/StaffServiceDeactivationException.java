package pl.pas.gr3.cinema.exceptions.services.crud.staff;

import pl.pas.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceDeactivationException extends GeneralStaffServiceException {
    public StaffServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
