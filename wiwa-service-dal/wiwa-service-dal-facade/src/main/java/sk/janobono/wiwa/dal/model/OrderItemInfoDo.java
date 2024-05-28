package sk.janobono.wiwa.dal.model;

import lombok.Builder;

@Builder
public record OrderItemInfoDo(
        String name,
        String description,
        Integer quantity
) {
}
