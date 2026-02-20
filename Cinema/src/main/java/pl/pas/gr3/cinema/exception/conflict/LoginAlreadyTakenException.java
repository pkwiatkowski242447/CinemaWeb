package pl.pas.gr3.cinema.exception.conflict;

import pl.pas.gr3.cinema.exception.general.ConflictException;
import pl.pas.gr3.cinema.util.I18n;

public class LoginAlreadyTakenException extends ConflictException {

    public LoginAlreadyTakenException() {
        super(I18n.LOGIN_ALREADY_TAKEN);
    }

    public LoginAlreadyTakenException(Throwable throwable) {
        super(I18n.LOGIN_ALREADY_TAKEN, throwable);
    }
}
