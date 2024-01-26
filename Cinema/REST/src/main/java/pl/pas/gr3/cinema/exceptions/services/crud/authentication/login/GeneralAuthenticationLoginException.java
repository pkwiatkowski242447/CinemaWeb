package pl.pas.gr3.cinema.exceptions.services.crud.authentication.login;

import pl.pas.gr3.cinema.exceptions.services.crud.authentication.GeneralAuthenticationServiceException;

public class GeneralAuthenticationLoginException extends GeneralAuthenticationServiceException {
    public GeneralAuthenticationLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
