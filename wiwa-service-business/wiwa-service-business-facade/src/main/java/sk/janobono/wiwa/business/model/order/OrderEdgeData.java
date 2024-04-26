package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderEdgeData(
        Long id,
        String code,
        String name,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {
}
