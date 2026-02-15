package pl.pas.gr3.cinema.exception.services.crud.authentication.register;

public class AuthenticationServiceUserWithGivenLoginExistsException extends GeneralAuthenticationRegisterException {
    public AuthenticationServiceUserWithGivenLoginExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
