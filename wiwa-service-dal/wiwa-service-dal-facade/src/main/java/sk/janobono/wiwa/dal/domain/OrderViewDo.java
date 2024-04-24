package sk.janobono.wiwa.dal.domain;

import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderViewDo(
        Long id,
        Long userId,
        LocalDateTime created,
        Long orderNumber,
        LocalDate delivery,
        OrderStatus status,
        BigDecimal weight,
        BigDecimal total
) {
}
