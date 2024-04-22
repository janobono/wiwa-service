package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderSearchCriteriaData(
        List<Long> userIds,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        List<OrderStatus> statuses,
        Money totalFrom,
        Money totalTo
) {
}
