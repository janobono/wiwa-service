package sk.janobono.wiwa.dal.domain;

import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderViewDo(
        Long id,
        Long userId,
        LocalDateTime created,
        Long orderNumber,
        String contact,
        LocalDate delivery,
        OrderPackageType packageType,
        OrderStatus status,
        BigDecimal weight,
        BigDecimal total
) {
}
