package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
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
        final OrderWebDto order = orderWebMapper.mapToWebDto(orderService.getOrder(id));
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (isNotCreator(order, user) && !isEmployee(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return order;
    }

    public OrderWebDto addOrder() {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderWebMapper.mapToWebDto(orderService.addOrder(user.id()));
    }

    public OrderWebDto sendOrder(final Long id, final SendOrderWebDto sendOrder) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.sendOrder(id, orderWebMapper.mapToData(sendOrder)));
    }

    public OrderWebDto setOrderStatus(final Long id, final OrderStatusChangeWebDto orderStatusChange) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isManager(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.setOrderStatus(id, orderWebMapper.mapToData(orderStatusChange)));
    }

    public List<OrderCommentWebDto> getComments(final Long id) {
        checkAccess(id);
        return orderService.getComments(id).stream().map(orderWebMapper::mapToWebDto).toList();
    }

    public List<OrderCommentWebDto> addComment(final Long id, final OrderCommentChangeWebDto commentChange) {
        final User user = checkAccess(id);
        return orderService.addComment(id, user.id(), orderWebMapper.mapToData(commentChange)).stream().map(orderWebMapper::mapToWebDto).toList();
    }

    public OrderItemDetailWebDto addItem(final Long id, final OrderItemWebDto orderItem) {
        final User user = checkAccess(id);
        return orderWebMapper.mapToWebDto(orderService.addItem(id, user.id(), orderWebMapper.mapToData(orderItem)));
    }

    public OrderItemDetailWebDto setItem(final Long id, final Long itemId, final OrderItemWebDto orderItem) {
        final User user = checkAccess(id);
        return orderWebMapper.mapToWebDto(orderService.setItem(id, itemId, user.id(), orderWebMapper.mapToData(orderItem)));
    }

    public void moveUpItem(final Long id, final Long itemId) {
        final User user = checkAccess(id);
        orderService.moveUpItem(id, itemId, user.id());
    }

    public void moveDownItem(final Long id, final Long itemId) {
        final User user = checkAccess(id);
        orderService.moveDownItem(id, itemId, user.id());
    }

    public void deleteItem(final Long id, final Long itemId) {
        final User user = checkAccess(id);
        orderService.deleteItem(id, itemId, user.id());
    }

    public void deleteOrder(final Long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final OrderWebDto order = orderWebMapper.mapToWebDto(orderService.getOrder(id));

        if (isManager(user)) {
            orderService.deleteOrder(id);
            return;
        }

        if (isNotCreator(order, user) || order.status() != OrderStatus.NEW) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        orderService.deleteOrder(id);
    }

    private List<Long> checkUserIds(final User user, final List<Long> userIds) {
        if (isEmployee(user)) {
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

    private User checkAccess(final Long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user) && !isEmployee(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return user;
    }

    private boolean isNotCreator(final OrderWebDto order, final User user) {
        return Optional.ofNullable(order)
                .map(OrderWebDto::creator)
                .map(OrderUserWebDto::id)
                .map(creatorId -> isNotCreator(creatorId, user))
                .orElse(true);
    }

    private boolean isNotCreator(final Long creatorId, final User user) {
        return !Optional.ofNullable(creatorId).map(id -> id.equals(user.id())).orElse(false);
    }

    private boolean isManager(final User user) {
        return authUtil.hasAnyAuthority(user, Authority.W_ADMIN, Authority.W_MANAGER);
    }

    private boolean isEmployee(final User user) {
        return authUtil.hasAnyAuthority(user, Authority.W_ADMIN, Authority.W_MANAGER, Authority.W_EMPLOYEE);
    }
}
