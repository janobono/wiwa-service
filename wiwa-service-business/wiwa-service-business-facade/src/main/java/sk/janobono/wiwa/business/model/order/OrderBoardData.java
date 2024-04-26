package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderBoardData(
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
