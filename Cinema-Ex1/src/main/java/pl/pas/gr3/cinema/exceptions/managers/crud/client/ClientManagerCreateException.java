package pl.pas.gr3.cinema.exceptions.managers.crud.client;

import pl.pas.gr3.cinema.exceptions.managers.GeneralClientManagerException;

public class ClientManagerCreateException extends GeneralClientManagerException {
    public ClientManagerCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
