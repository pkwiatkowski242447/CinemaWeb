package pl.pas.gr3.cinema.exceptions.services.crud.admin;

import pl.pas.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceDeactivationException extends GeneralAdminServiceException {
    public AdminServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
