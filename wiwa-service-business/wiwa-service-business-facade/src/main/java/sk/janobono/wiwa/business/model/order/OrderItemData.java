package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemData(
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
