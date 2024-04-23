package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCommentData(
        Long id,
        Long parentId,
        OrderUserData creator,
        LocalDateTime created,
        String comment
) {
}
