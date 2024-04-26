package sk.janobono.wiwa.api.model.order;

import java.math.BigDecimal;

public record OrderBoardWebDto(
        Long id,
        String code,
        String name,
        String boardCode,
        String structureCode,
        Boolean orientation,
        BigDecimal weight,
        BigDecimal length,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {
}
