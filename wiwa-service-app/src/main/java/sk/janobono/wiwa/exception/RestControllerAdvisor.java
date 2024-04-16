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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RestControllerAdvice
public class RestControllerAdvisor {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException handlerFoundException) {
        log.warn(handlerFoundException.toString(), handlerFoundException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.NOT_FOUND.name(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(final AuthenticationException authenticationException) {
        log.warn(authenticationException.toString(), authenticationException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.UNAUTHORIZED.name(),
                authenticationException.getMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException accessDeniedException) {
        log.warn(accessDeniedException.toString(), accessDeniedException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.FORBIDDEN.name(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(final ApplicationException applicationException) {
        log.warn(applicationException.toString(), applicationException);
        return new ResponseEntity<>(
                new ExceptionBody(
                        applicationException.getCode(),
                        applicationException.getMessage(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(final ResponseStatusException responseStatusException) {
        log.warn(responseStatusException.toString(), responseStatusException);
        return new ResponseEntity<>(
                new ExceptionBody(
                        "UNKNOWN",
                        responseStatusException.getReason(),
                        LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
                ), responseStatusException.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(final Exception exception) {
        log.warn(exception.toString(), exception);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(final RuntimeException runtimeException) {
        log.warn(runtimeException.toString(), runtimeException);
        return new ResponseEntity<>(new ExceptionBody(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
