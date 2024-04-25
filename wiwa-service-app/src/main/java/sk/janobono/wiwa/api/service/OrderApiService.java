package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
import sk.janobono.wiwa.api.model.ResourceEntityWebDto;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.AuthUtil;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class OrderApiService {

    private final AuthUtil authUtil;

    private final OrderService orderService;
    private final OrderWebMapper orderWebMapper;

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

    public OrderWebDto setOrderContact(final long id, final OrderContactWebDto orderContact) {
        return orderWebMapper.mapToWebDto(orderService.setOrderContact(id, orderWebMapper.mapToData(orderContact)));
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

    public OrderWebDto recountOrder(final long id) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.recountOrder(id, user.id()));
    }

    public ResourceEntityWebDto getPdf(final long id) {
        checkEmployeeAccess(id);
        return new ResourceEntityWebDto(
                "order[%d].pdf".formatted(id),
                "application/pdf",
                new ByteArrayResource(orderService.getPdf(id))
        );
    }

    public ResourceEntityWebDto getCsv(final long id) {
        return new ResourceEntityWebDto(
                "order[%d].csv".formatted(id),
                "text/csv",
                new ByteArrayResource(orderService.getCsv(id))
        );
    }

    public OrderWebDto sendOrder(final Long id, final SendOrderWebDto sendOrder) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.sendOrder(id, user.id(), orderWebMapper.mapToData(sendOrder)));
    }

    public OrderWebDto setOrderStatus(final Long id, final OrderStatusChangeWebDto orderStatusChange) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isManager(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return orderWebMapper.mapToWebDto(orderService.setOrderStatus(id, user.id(), orderWebMapper.mapToData(orderStatusChange)));
    }

    public OrderWebDto addComment(final Long id, final OrderCommentChangeWebDto commentChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.addComment(id, user.id(), orderWebMapper.mapToData(commentChange)));
    }

    public OrderWebDto addItem(final Long id, final OrderItemChangeWebDto orderItemChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.addItem(id, user.id(), orderWebMapper.mapToData(orderItemChange), isManager(user)));
    }

    public OrderWebDto setItem(final Long id, final Long itemId, final OrderItemChangeWebDto orderItemChange) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.setItem(id, itemId, user.id(), orderWebMapper.mapToData(orderItemChange), isManager(user)));
    }

    public OrderWebDto moveUpItem(final Long id, final Long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.moveUpItem(id, itemId, user.id(), isManager(user)));
    }

    public OrderWebDto moveDownItem(final Long id, final Long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.moveDownItem(id, itemId, user.id(), isManager(user)));
    }

    public OrderWebDto deleteItem(final Long id, final Long itemId) {
        final User user = checkManagerAccess(id);
        return orderWebMapper.mapToWebDto(orderService.deleteItem(id, itemId, user.id(), isManager(user)));
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

    private User checkManagerAccess(final Long id) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Long creatorId = orderService.getOrderCreatorId(id);
        if (isNotCreator(creatorId, user) && !isManager(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return user;
    }

    private User checkEmployeeAccess(final Long id) {
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
