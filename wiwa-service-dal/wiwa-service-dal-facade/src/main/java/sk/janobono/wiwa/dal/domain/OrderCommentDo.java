package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class OrderCommentDo {
    private Long id;
    private Long orderId;
    private Long userId;
    private LocalDateTime created;
    private String comment;
}
