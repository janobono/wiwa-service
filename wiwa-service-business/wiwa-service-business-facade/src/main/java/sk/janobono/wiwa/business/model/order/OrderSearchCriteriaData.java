package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderSearchCriteriaData(
        List<Long> userIds,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        List<OrderStatus> statuses,
        BigDecimal totalFrom,
        BigDecimal totalTo,
        Unit totalUnit
) {
}
