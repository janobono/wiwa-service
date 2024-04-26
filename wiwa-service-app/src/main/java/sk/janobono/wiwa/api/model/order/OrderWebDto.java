package sk.janobono.wiwa.api.model.order;

import org.springframework.format.annotation.DateTimeFormat;
import sk.janobono.wiwa.api.model.order.item.OrderItemWebDto;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderWebDto(
        Long id,
        OrderUserWebDto creator,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime created,
        OrderStatus status,
        Long orderNumber,
        BigDecimal weight,
        BigDecimal total,
        BigDecimal vatTotal,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
        OrderPackageType packageType,
        List<OrderItemWebDto> items,
        OrderSummaryWebDto summary,
        List<OrderCommentWebDto> comments
) {
}
