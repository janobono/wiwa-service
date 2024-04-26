package sk.janobono.wiwa.api.model.edge;

import java.math.BigDecimal;
import java.util.List;

public record EdgeWebDto(
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
        List<EdgeCategoryItemWebDto> categoryItems
) {
}
