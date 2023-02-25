package sk.janobono.wiwa.common.exception;

import java.time.LocalDateTime;

public record ExceptionBody(String code, String message, LocalDateTime timestamp) {
}
