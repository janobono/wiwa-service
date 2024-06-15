package sk.janobono.wiwa.api.model.board;

import sk.janobono.wiwa.api.model.CategoryItemWebDto;

import java.math.BigDecimal;
import java.util.List;

public record BoardWebDto(
        Long id,
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        BigDecimal sale,
        BigDecimal weight,
        BigDecimal length,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price,
        BigDecimal vatPrice,
        List<CategoryItemWebDto> categoryItems
) {
}
