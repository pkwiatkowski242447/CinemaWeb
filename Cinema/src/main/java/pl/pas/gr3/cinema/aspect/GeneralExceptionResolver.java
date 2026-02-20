package pl.pas.gr3.cinema.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.pas.gr3.cinema.dto.ConstraintViolationExceptionResponse;
import pl.pas.gr3.cinema.dto.ExceptionResponse;
import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.exception.general.ConflictException;
import pl.pas.gr3.cinema.exception.general.ConstraintViolationException;
import pl.pas.gr3.cinema.exception.general.ForbiddenException;
import pl.pas.gr3.cinema.exception.general.InternalServerError;
import pl.pas.gr3.cinema.exception.general.NotFoundException;
import pl.pas.gr3.cinema.exception.general.PreConditionFailedException;
import pl.pas.gr3.cinema.exception.general.UnauthorizedException;
import pl.pas.gr3.cinema.util.I18n;

@Slf4j
@ControllerAdvice
@Order(10)
public class GeneralExceptionResolver {

    @ExceptionHandler({BadRequestException.class})
    ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    ResponseEntity<ExceptionResponse> handleConstraintValidationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ConstraintViolationExceptionResponse(exception));
    }

    @ExceptionHandler({ConflictException.class})
    ResponseEntity<ExceptionResponse> handleConflictException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({UnauthorizedException.class})
    ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({ForbiddenException.class})
    ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({NotFoundException.class})
    ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({PreConditionFailedException.class})
    ResponseEntity<ExceptionResponse> handlePreConditionFailedException(PreConditionFailedException exception) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({InternalServerError.class})
    ResponseEntity<ExceptionResponse> handleInternalServerErrorException(InternalServerError exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(exception));
    }

    /* Other exceptions */

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ExceptionResponse(I18n.APP_ACCESS_DENIED));
    }

}
