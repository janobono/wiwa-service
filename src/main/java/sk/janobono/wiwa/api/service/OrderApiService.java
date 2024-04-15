package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.AuthUtil;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.User;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderApiService {

    private final AuthUtil authUtil;

    private final OrderService orderService;
    private final OrderWebMapper orderWebMapper;

    public Page<OrderWebDto> getOrders(
            final List<Long> userIds,
            final ZonedDateTime createdFrom,
            final ZonedDateTime createdTo,
            final List<OrderStatus> statuses,
            final BigDecimal totalFrom,
            final BigDecimal totalTo,
            final Unit totalUnit,
            final Pageable pageable
    ) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final OrderSearchCriteriaData criteria = OrderSearchCriteriaData.builder()
                .userIds(checkUserIds(user, userIds))
                .createdFrom(createdFrom)
                .createdTo(createdTo)
                .statuses(statuses)
                .totalFrom(totalFrom)
                .totalTo(totalTo)
                .totalUnit(totalUnit)
                .build();
        return orderService.getOrders(criteria, pageable).map(orderWebMapper::mapToWebDto);
    }

    private List<Long> checkUserIds(final User user, final List<Long> userIds) {
        if (authUtil.hasAnyAuthority(user, Authority.W_ADMIN, Authority.W_MANAGER, Authority.W_EMPLOYEE)) {
            return userIds;
        }
        if (Optional.ofNullable(userIds).map(List::isEmpty).orElse(false)) {
            return List.of(user.id());
        }
        if (Optional.ofNullable(userIds).stream().flatMap(Collection::stream).anyMatch(id -> !Objects.equals(id, user.id()))) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return userIds;
    }
}
