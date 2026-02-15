package pl.pas.gr3.cinema.exception.services.crud.staff;

import pl.pas.gr3.cinema.exception.services.GeneralStaffServiceException;

public class StaffServiceActivationException extends GeneralStaffServiceException {
    public StaffServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
