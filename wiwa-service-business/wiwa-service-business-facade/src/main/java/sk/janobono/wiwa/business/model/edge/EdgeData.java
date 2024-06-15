package sk.janobono.wiwa.business.model.edge;

import lombok.Builder;
import sk.janobono.wiwa.business.model.CategoryItemData;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record EdgeData(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal sale,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price,
        BigDecimal vatPrice,
        List<CategoryItemData> categoryItems
) {
}
