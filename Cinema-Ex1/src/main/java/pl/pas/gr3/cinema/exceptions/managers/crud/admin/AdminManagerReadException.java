package pl.pas.gr3.cinema.exceptions.managers.crud.admin;

import pl.pas.gr3.cinema.exceptions.managers.GeneralAdminManagerException;

public class AdminManagerReadException extends GeneralAdminManagerException {
    public AdminManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
