package pl.pas.gr3.cinema.exceptions.managers.crud.client;

import pl.pas.gr3.cinema.exceptions.managers.GeneralClientManagerException;

public class ClientManagerDeactivationException extends GeneralClientManagerException {
    public ClientManagerDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
