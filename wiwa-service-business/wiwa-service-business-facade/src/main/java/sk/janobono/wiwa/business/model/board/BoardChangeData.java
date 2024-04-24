package sk.janobono.wiwa.business.model.board;

import java.math.BigDecimal;

public record BoardChangeData(
        String code,
        String name,
        String description,
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
