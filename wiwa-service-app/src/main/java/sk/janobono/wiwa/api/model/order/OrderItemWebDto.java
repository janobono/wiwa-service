package sk.janobono.wiwa.api.model.order;

import java.math.BigDecimal;

public record OrderItemWebDto(
        Long id,
        Integer sortNum,
        String name,
        BigDecimal partPrice,
        BigDecimal partNetWeight,
        BigDecimal amount,
        BigDecimal netWeight,
        BigDecimal total
) {
}
