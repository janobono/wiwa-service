package sk.janobono.wiwa.api.model.order;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record OrderCommentWebDto(
        Long id,
        OrderUserWebDto creator,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime created,
        String comment,
        List<OrderCommentWebDto> subComments
) {
}
