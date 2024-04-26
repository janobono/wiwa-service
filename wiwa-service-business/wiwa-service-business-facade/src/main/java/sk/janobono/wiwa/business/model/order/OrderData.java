package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.item.OrderItemData;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderData(
        Long id,
        OrderUserData creator,
        LocalDateTime created,
        OrderStatus status,
        Long orderNumber,
        BigDecimal weight,
        BigDecimal total,
        BigDecimal vatTotal,
        LocalDate deliveryDate,
        OrderPackageType packageType,
        List<OrderItemData> items,
        OrderSummaryData summary,
        List<OrderCommentData> comments
) {
}
