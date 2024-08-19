package pl.pas.gr3.cinema.interceptors.controllers;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.pas.gr3.cinema.common.dto.ExceptionOutputDTO;
import pl.pas.gr3.cinema.exceptions.authentication.ApplicationInvalidAuthenticationException;

@ControllerAdvice
@Order(5)
public class GeneralExceptionResolver {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ApplicationInvalidAuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationExceptions(Exception exception) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ExceptionOutputDTO(exception.getMessage()));
    }
}
