package pl.pas.gr3.cinema.exception.general;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pl.pas.gr3.cinema.exception.ApplicationBaseException;
import pl.pas.gr3.cinema.util.I18n;

import java.util.Set;

@Getter
public class ConstraintViolationException extends ApplicationBaseException {

    private final Set<String> violations;

    public ConstraintViolationException(Set<String> violations) {
        super(I18n.APP_CONSTRAINT_VIOLATION, HttpStatus.BAD_REQUEST);
        this.violations = violations;
    }
}
