package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSearchCriteriaDo(
        List<Long> userIds,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        List<OrderStatus> statuses,
        Money totalFrom,
        Money totalTo
) {
}
