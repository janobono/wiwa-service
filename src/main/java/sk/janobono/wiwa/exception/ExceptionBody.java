package sk.janobono.wiwa.exception;

import java.time.LocalDateTime;

public record ExceptionBody(String code, String message, LocalDateTime timestamp) {
}
