package pl.pas.gr3.cinema.exception.services.crud.admin;

import pl.pas.gr3.cinema.exception.services.GeneralAdminServiceException;

public class AdminServiceUpdateException extends GeneralAdminServiceException {
    public AdminServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
