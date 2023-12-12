package pl.pas.gr3.cinema.exceptions.services.crud.client;

import pl.pas.gr3.cinema.exceptions.services.GeneralClientServiceException;

public class ClientServiceDeleteException extends GeneralClientServiceException {
    public ClientServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
