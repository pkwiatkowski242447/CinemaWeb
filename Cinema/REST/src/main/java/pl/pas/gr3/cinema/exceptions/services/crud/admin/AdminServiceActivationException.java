package pl.pas.gr3.cinema.exceptions.services.crud.admin;

import pl.pas.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceActivationException extends GeneralAdminServiceException {
    public AdminServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
