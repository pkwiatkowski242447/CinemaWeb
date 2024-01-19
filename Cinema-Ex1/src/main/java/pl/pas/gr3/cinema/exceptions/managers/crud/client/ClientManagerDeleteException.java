package pl.pas.gr3.cinema.exceptions.managers.crud.client;

import pl.pas.gr3.cinema.exceptions.managers.GeneralClientManagerException;

public class ClientManagerDeleteException extends GeneralClientManagerException {
    public ClientManagerDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
