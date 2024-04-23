package sk.janobono.wiwa.api.model.order;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record OrderCommentWebDto(
        Long id,
        OrderUserWebDto creator,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime created,
        String comment
) {
}
