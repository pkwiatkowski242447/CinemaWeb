package pl.pas.gr3.cinema.exceptions.services.crud.admin;

import pl.pas.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceReadException extends GeneralAdminServiceException {
    public AdminServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
