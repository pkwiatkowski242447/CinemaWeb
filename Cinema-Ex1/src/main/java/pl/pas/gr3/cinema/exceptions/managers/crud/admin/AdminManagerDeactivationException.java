package pl.pas.gr3.cinema.exceptions.managers.crud.admin;

import pl.pas.gr3.cinema.exceptions.managers.GeneralAdminManagerException;

public class AdminManagerDeactivationException extends GeneralAdminManagerException {
    public AdminManagerDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
