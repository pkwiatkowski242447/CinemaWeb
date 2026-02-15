package pl.pas.gr3.cinema.exception.services.crud.client;

import pl.pas.gr3.cinema.exception.services.GeneralClientServiceException;

public class ClientServiceDeactivationException extends GeneralClientServiceException {
    public ClientServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
