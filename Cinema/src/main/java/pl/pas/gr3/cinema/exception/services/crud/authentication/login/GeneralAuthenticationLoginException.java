package pl.pas.gr3.cinema.exception.services.crud.authentication.login;

import pl.pas.gr3.cinema.exception.services.crud.authentication.GeneralAuthenticationServiceException;

public class GeneralAuthenticationLoginException extends GeneralAuthenticationServiceException {
    public GeneralAuthenticationLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
