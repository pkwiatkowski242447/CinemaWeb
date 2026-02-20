package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class InternalServerError extends ApplicationBaseException {

    public InternalServerError(String key) {
        super(key, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
