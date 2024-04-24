package sk.janobono.wiwa.business.model.edge;

import java.math.BigDecimal;

public record EdgeChangeData(
        String code,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {
}
