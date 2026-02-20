package pl.pas.gr3.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.exception.general.ConstraintViolationException;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConstraintViolationExceptionResponse extends ExceptionResponse {

    private Set<String> violations = new HashSet<>();

    public ConstraintViolationExceptionResponse(ConstraintViolationException exception) {
        super(exception);
        this.violations = exception.getViolations();
    }

    public ConstraintViolationExceptionResponse(String key, Set<String> violations) {
        super(key);
        this.violations = violations;
    }

}
