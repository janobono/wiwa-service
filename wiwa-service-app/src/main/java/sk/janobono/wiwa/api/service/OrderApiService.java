package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;
import sk.janobono.wiwa.business.model.order.OrderUserSearchCriteriaData;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.AuthUtil;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderApiService {

    private final AuthUtil authUtil;

    private final OrderService orderService;
    private final OrderWebMapper orderWebMapper;
    private final ApplicationImageWebMapper applicationImageWebMapper;

    public Page<OrderWebDto> getOrders(
            final Set<Long> userIds,
            final LocalDateTime createdFrom,
            final LocalDateTime createdTo,
            final Set<OrderStatus> statuses,
            final BigDecimal totalFrom,
            final BigDecimal totalTo,
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
                .build();
        return orderService.getOrders(criteria, pageable).map(orderWebMapper::mapToWebDto);
    }

    public Page<OrderContactWebDto> getOrderContacts(final Pageable pageable) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getOrderContacts(user.id(), pageable).map(orderWebMapper::mapToWebDto);
    }

    public Page<OrderUserWebDto> getOrderUsers(final String searchField, final String email, final Pageable pageable) {
        final OrderUserSearchCriteriaData criteria = OrderUserSearchCriteriaData.builder()
                .searchField(searchField)
                .email(email)
                .build();
        return orderService.getOrderUsers(criteria, pageable).map(orderWebMapper::mapToWebDto);
    }

    public OrderWebDto setOrderContact(final long id, final OrderContactWebDto orderContact) {
        return orderWebMapper.mapToWebDto(orderService.setOrderContact(id, orderWebMapper.mapToData(orderContact)));
    }

    public OrderWebDto getOrder(final long id) {
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

    public OrderWebDto recountOrder(final long id) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.recountOrder(id, user.id()));
    }

    public SingleValueBodyWebDto<String> getHtml(final long id) {
        checkEmployeeAccess(id);
        return new SingleValueBodyWebDto<>(orderService.getHtml(id));
    }

    public SingleValueBodyWebDto<String> getCsv(final long id) {
        return new SingleValueBodyWebDto<>(orderService.getCsv(id));
    }

    public OrderWebDto sendOrder(final long id, final SendOrderWebDto sendOrder) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.sendOrder(id, user.id(), orderWebMapper.mapToData(sendOrder)));
    }

    public OrderWebDto setOrderStatus(final long id, final OrderStatusChangeWebDto orderStatusChange) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isManager(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.setOrderStatus(id, user.id(), orderWebMapper.mapToData(orderStatusChange)));
    }

    public OrderWebDto addComment(final long id, final OrderCommentChangeWebDto commentChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.addComment(id, user.id(), orderWebMapper.mapToData(commentChange)));
    }

    public OrderWebDto addItem(final long id, final OrderItemChangeWebDto orderItemChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.addItem(id, user.id(), orderWebMapper.mapToData(orderItemChange), isManager(user)));
    }

    public OrderWebDto setItem(final long id, final long itemId, final OrderItemChangeWebDto orderItemChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.setItem(id, itemId, user.id(), orderWebMapper.mapToData(orderItemChange), isManager(user)));
    }

    public OrderWebDto moveUpItem(final long id, final long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.moveUpItem(id, itemId, user.id(), isManager(user)));
    }

    public OrderWebDto moveDownItem(final long id, final long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.moveDownItem(id, itemId, user.id(), isManager(user)));
    }

    public List<OrderItemImageWebDto> getItemImages(final long id, final long itemId) {
        checkEmployeeAccess(id);
        return orderService.getItemImages(id, itemId).stream().map(applicationImageWebMapper::mapToWebDto).toList();
    }

    public OrderWebDto deleteItem(final long id, final long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.deleteItem(id, itemId, user.id(), isManager(user)));
    }

    public void deleteOrder(final long id) {
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

    private Set<Long> checkUserIds(final User user, final Set<Long> userIds) {
        if (isEmployee(user)) {
            return userIds;
        }
        if (Optional.ofNullable(userIds).map(Set::isEmpty).orElse(false)) {
            return Set.of(user.id());
        }
        if (Optional.ofNullable(userIds).stream().flatMap(Collection::stream).anyMatch(id -> !Objects.equals(id, user.id()))) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return userIds;
    }

    private User checkManagerAccess(final long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user) && !isManager(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return user;
    }

    private void checkEmployeeAccess(final long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user) && !isEmployee(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
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
