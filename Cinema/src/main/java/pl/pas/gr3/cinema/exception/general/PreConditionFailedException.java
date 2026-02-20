package pl.pas.gr3.cinema.exception.general;

import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;

public class PreConditionFailedException extends ApplicationBaseException {

    public PreConditionFailedException(String key) {
        super(key, HttpStatus.PRECONDITION_FAILED);
    }
}
