package pl.pas.gr3.cinema.exception.services.crud.admin;

import pl.pas.gr3.cinema.exception.services.GeneralAdminServiceException;

public class AdminServiceDeleteException extends GeneralAdminServiceException {
    public AdminServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
