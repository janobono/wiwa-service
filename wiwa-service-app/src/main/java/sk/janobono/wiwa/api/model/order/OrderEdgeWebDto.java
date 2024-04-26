package sk.janobono.wiwa.api.model.order;

import java.math.BigDecimal;

public record OrderEdgeWebDto(
        Long id,
        String code,
        String name,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {
}
