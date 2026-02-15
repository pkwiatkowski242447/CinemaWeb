package pl.pas.gr3.cinema.exception.services.crud.client;

import pl.pas.gr3.cinema.exception.services.GeneralClientServiceException;

public class ClientServiceUpdateException extends GeneralClientServiceException {
    public ClientServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
