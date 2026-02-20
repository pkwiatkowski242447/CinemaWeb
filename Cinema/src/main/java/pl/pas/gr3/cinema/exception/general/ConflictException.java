package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class ConflictException extends ApplicationBaseException {

    public ConflictException(String key) {
        super(key, HttpStatus.CONFLICT);
    }

    public ConflictException(String key, Throwable throwable) {
        super(key, HttpStatus.CONFLICT, throwable);
    }
}
