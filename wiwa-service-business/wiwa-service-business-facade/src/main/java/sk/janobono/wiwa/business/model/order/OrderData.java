package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.item.OrderItemData;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;

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
        Quantity weight,
        Money total,
        Money vatTotal,
        LocalDate deliveryDate,
        OrderPackageType packageType,
        List<OrderItemData> items,
        OrderSummaryData summary,
        List<OrderCommentData> comments
) {
}
