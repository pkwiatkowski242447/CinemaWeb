package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class UnauthorizedException extends ApplicationBaseException {

    public UnauthorizedException(String key) {
        super(key, HttpStatus.UNAUTHORIZED);
    }
}
