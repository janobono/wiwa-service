package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
import sk.janobono.wiwa.api.mapper.OrderWebMapperImpl;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.AuthUtil;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            final LocalDateTime createdFrom,
            final LocalDateTime createdTo,
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

    public Page<OrderContactWebDto> getOrderContacts(final Pageable pageable) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getOrderContacts(user.id(), pageable).map(orderWebMapper::mapToWebDto);
    }

    public OrderWebDto getOrder(final Long id) {
        final OrderWebDto result = orderWebMapper.mapToWebDto(orderService.getOrder(id));
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!result.creator().id().equals(user.id()) && !authUtil.hasAnyAuthority(user, Authority.W_ADMIN, Authority.W_MANAGER, Authority.W_EMPLOYEE)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return result;
    }

    public OrderWebDto addOrder(final OrderCommentChangeWebDto orderCommentChange) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderWebMapper.mapToWebDto(orderService.addOrder(user.id(), orderWebMapper.mapToData(orderCommentChange)));
    }

    public OrderWebDto sendOrder(SendOrderWebDto sendOrder) {
        // TODO
        return null;
    }

    public OrderWebDto setOrderStatus(OrderStatusChangeWebDto orderStatusChange) {
        // TODO
        return null;
    }

    public List<OrderCommentWebDto> getComments(Long id) {
        // TODO
        return null;
    }

    public List<OrderCommentWebDto> addComment(Long id, OrderCommentChangeWebDto commentChange) {
        // TODO
        return null;
    }

    public OrderItemDetailWebDto addItem(Long id, OrderItemWebDto orderItem) {
        // TODO
        return null;
    }

    public OrderItemDetailWebDto setItem(Long id, Long itemId, OrderItemWebDto orderItem) {
        // TODO
        return null;
    }

    public void moveUpItem(Long id, Long itemId) {
        // TODO
    }

    public void moveDownItem(Long id, Long itemId) {
        // TODO
    }

    public void deleteItem(Long id, Long itemId) {
        // TODO
    }

    public void deleteOrder(final Long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final OrderWebDto orderWebDto = orderWebMapper.mapToWebDto(orderService.getOrder(id));

        if (authUtil.hasAnyAuthority(user, Authority.W_ADMIN, Authority.W_MANAGER)) {
            orderService.deleteOrder(id);
            return;
        }

        if (!orderWebDto.creator().id().equals(user.id()) || orderWebDto.status() != OrderStatus.NEW) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        orderService.deleteOrder(id);
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
