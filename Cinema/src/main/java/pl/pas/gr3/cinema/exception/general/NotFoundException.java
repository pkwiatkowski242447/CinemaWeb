package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class NotFoundException extends ApplicationBaseException {

    public NotFoundException(String key) {
        super(key, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String key, Throwable cause) {
        super(key, HttpStatus.NOT_FOUND, cause);
    }
}
