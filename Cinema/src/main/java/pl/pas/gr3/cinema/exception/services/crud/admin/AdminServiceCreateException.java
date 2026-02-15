package pl.pas.gr3.cinema.exception.services.crud.admin;

import pl.pas.gr3.cinema.exception.services.GeneralAdminServiceException;

public class AdminServiceCreateException extends GeneralAdminServiceException {

    public AdminServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
