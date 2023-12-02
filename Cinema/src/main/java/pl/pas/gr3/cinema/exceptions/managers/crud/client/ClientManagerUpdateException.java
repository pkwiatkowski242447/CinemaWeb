package pl.pas.gr3.cinema.exceptions.managers.crud.client;

import pl.pas.gr3.cinema.exceptions.managers.GeneralClientManagerException;

public class ClientManagerUpdateException extends GeneralClientManagerException {
    public ClientManagerUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
