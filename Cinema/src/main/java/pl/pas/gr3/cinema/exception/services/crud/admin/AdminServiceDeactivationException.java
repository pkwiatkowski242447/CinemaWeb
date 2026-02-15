package pl.pas.gr3.cinema.exception.services.crud.admin;

import pl.pas.gr3.cinema.exception.services.GeneralAdminServiceException;

public class AdminServiceDeactivationException extends GeneralAdminServiceException {
    public AdminServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
