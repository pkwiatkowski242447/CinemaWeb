package pl.pas.gr3.cinema.exceptions.managers.crud.client;

import pl.pas.gr3.cinema.exceptions.managers.GeneralClientManagerException;

public class ClientManagerReadException extends GeneralClientManagerException {
    public ClientManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
