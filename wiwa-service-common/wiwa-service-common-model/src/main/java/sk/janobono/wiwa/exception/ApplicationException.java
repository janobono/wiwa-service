package sk.janobono.wiwa.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final String code;

    public ApplicationException(final String code, final String message, final Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
