package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSearchCriteriaDo(
        List<Long> userIds,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        List<OrderStatus> statuses,
        BigDecimal totalFrom,
        BigDecimal totalTo,
        Unit totalUnit
) {
}
