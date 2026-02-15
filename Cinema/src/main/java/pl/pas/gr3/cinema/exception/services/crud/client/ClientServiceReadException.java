package pl.pas.gr3.cinema.exception.services.crud.client;

import pl.pas.gr3.cinema.exception.services.GeneralClientServiceException;

public class ClientServiceReadException extends GeneralClientServiceException {
    public ClientServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
