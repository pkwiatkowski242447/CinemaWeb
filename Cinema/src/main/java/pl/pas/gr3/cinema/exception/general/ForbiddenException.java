package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class ForbiddenException extends ApplicationBaseException {

    public ForbiddenException(String key) {
        super(key, HttpStatus.FORBIDDEN);
    }
}
