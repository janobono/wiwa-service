package sk.janobono.wiwa.dal.model;

import lombok.Builder;

@Builder
public record OrderMaterialIdDo(
        Long orderId,
        Long materialId,
        String code
) {
}
