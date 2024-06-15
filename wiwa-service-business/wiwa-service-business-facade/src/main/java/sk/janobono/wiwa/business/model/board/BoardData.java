package sk.janobono.wiwa.business.model.board;

import lombok.Builder;
import sk.janobono.wiwa.business.model.CategoryItemData;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BoardData(
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
        List<CategoryItemData> categoryItems
) {
}
