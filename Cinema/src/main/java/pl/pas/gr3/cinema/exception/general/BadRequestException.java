package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class BadRequestException extends ApplicationBaseException {

    public BadRequestException(String key) {
        super(key, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String key, Throwable cause) {
        super(key, HttpStatus.BAD_REQUEST, cause);
    }
}
