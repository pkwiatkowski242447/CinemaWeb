package pl.pas.gr3.cinema.exception.services.crud.authentication.register;

import pl.pas.gr3.cinema.exception.services.crud.authentication.GeneralAuthenticationServiceException;

public class GeneralAuthenticationRegisterException extends GeneralAuthenticationServiceException {
    public GeneralAuthenticationRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
