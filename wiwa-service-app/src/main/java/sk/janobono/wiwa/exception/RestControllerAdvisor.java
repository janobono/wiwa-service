package sk.janobono.wiwa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import sk.janobono.wiwa.common.exception.ApplicationException;
import sk.janobono.wiwa.common.exception.ExceptionBody;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RestControllerAdvice
public class RestControllerAdvisor {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException handlerFoundException) {
        log.warn("handleNoHandlerFoundException", handlerFoundException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.NOT_FOUND.name(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException authenticationException) {
        log.warn("handleAuthenticationException", authenticationException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.UNAUTHORIZED.name(),
                authenticationException.getMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        log.warn("handleAccessDeniedException", accessDeniedException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.FORBIDDEN.name(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException applicationException) {
        log.warn("handleApplicationException", applicationException);
        return new ResponseEntity<>(
                new ExceptionBody(
                        applicationException.getCode(),
                        applicationException.getMessage(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException responseStatusException) {
        log.warn("handleResponseStatusException", responseStatusException);
        return new ResponseEntity<>(
                new ExceptionBody(
                        "UNKNOWN",
                        responseStatusException.getReason(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
                ), responseStatusException.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.warn("handleException", exception);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException runtimeException) {
        log.warn("handleRuntimeException", runtimeException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
