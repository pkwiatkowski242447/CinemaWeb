package pl.pas.gr3.cinema.exception.services.crud.admin;

import pl.pas.gr3.cinema.exception.services.GeneralAdminServiceException;

public class AdminServiceReadException extends GeneralAdminServiceException {
    public AdminServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
