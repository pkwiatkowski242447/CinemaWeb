package pl.pas.gr3.cinema.exception.services.crud.client;

import pl.pas.gr3.cinema.exception.services.GeneralClientServiceException;

public class ClientServiceActivationException extends GeneralClientServiceException {
    public ClientServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
