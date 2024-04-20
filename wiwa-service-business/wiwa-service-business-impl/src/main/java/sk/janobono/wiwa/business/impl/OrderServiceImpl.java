package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.OrderAttributeUtil;
import sk.janobono.wiwa.business.impl.component.OrderCommentUtil;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderAttributeRepository;
import sk.janobono.wiwa.dal.repository.OrderContactRepository;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderAttributeKey;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final PriceUtil priceUtil;
    private final OrderAttributeUtil orderAttributeUtil;
    private final OrderCommentUtil orderCommentUtil;

    private final OrderRepository orderRepository;
    private final OrderAttributeRepository orderAttributeRepository;
    private final OrderContactRepository orderContactRepository;
    private final OrderNumberRepository orderNumberRepository;

    private final UserUtilService userUtilService;

    private final ApplicationPropertyService applicationPropertyService;

    @Override
    public Page<OrderData> getOrders(final OrderSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return orderRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toOrderData(value, vatRate));
    }

    @Override
    public Page<OrderContactData> getOrderContacts(final Long userId, final Pageable pageable) {
        return orderContactRepository.findByUserId(userId, pageable).map(value -> OrderContactData.builder()
                .name(value.name())
                .street(value.street())
                .zipCode(value.zipCode())
                .city(value.city())
                .state(value.state())
                .phone(value.phone())
                .email(value.email())
                .businessId(value.businessId())
                .taxId(value.taxId())
                .build());
    }

    @Override
    public OrderData getOrder(final Long id) {
        return toOrderData(getOrderDo(id), applicationPropertyService.getVatRate());
    }

    @Override
    public OrderData addOrder(final Long userId) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();

        final OrderDo orderDo = orderRepository.save(
                OrderDo.builder()
                        .userId(userId)
                        .created(LocalDateTime.now())
                        .status(OrderStatus.NEW)
                        .orderNumber(orderNumberRepository.getNextOrderNumber(userId))
                        .weightValue(BigDecimal.ZERO)
                        .weightUnit(Unit.KILOGRAM)
                        .netWeightValue(BigDecimal.ZERO)
                        .netWeightUnit(Unit.KILOGRAM)
                        .totalValue(BigDecimal.ZERO)
                        .totalUnit(Unit.EUR)
                        .build());

        return toOrderData(orderDo, vatRate);
    }

    @Override
    public void deleteOrder(final Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Long getOrderCreatorId(final Long id) {
        return orderRepository.getOrderUserId(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    @Override
    public OrderSummaryData getOrderSummary(final Long id) {
        return orderAttributeRepository.findByOrderIdAndAttributeKey(id, OrderAttributeKey.SUMMARY)
                .map(orderAttributeDo -> orderAttributeUtil.parseValue(orderAttributeDo, OrderSummaryData.class))
                .orElse(OrderSummaryData.builder()
                        // TODO
                        .build());
    }

    @Override
    public OrderData sendOrder(Long id, SendOrderData sendOrder) {
        return null;
    }

    @Override
    public OrderData setOrderStatus(Long id, OrderStatusChangeData orderStatusChange) {
        return null;
    }

    @Override
    public List<OrderCommentData> getComments(final Long id) {
        return orderAttributeRepository.findByOrderIdAndAttributeKey(id, OrderAttributeKey.COMMENTS)
                .map(orderAttributeDo -> Arrays.asList(orderAttributeUtil.parseValue(orderAttributeDo, OrderCommentData[].class)))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<OrderCommentData> addComment(final Long id, final Long creatorId, final OrderCommentChangeData orderCommentChange) {
        final UserDo userDo = userUtilService.getUserDo(creatorId);

        final List<OrderCommentData> orderComments = orderCommentUtil.addComment(
                getComments(id), toOrderUser(userDo), orderCommentChange.parentId(), orderCommentChange.comment()
        );

        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.COMMENTS, orderComments));

        return orderComments;
    }

    @Override
    public List<OrderItemData> getOrderItems(Long id) {
        return List.of();
    }

    @Override
    public OrderItemData addItem(Long id, Long creatorId, OrderItemChangeData orderItem) {
        return null;
    }

    @Override
    public OrderItemData setItem(Long id, Long itemId, Long modifierId, OrderItemChangeData orderItem) {
        return null;
    }

    @Override
    public OrderItemData moveUpItem(Long id, Long itemId, Long modifierId) {
        return null;
    }

    @Override
    public OrderItemData moveDownItem(Long id, Long itemId, Long modifierId) {
        return null;
    }

    @Override
    public void deleteItem(Long id, Long itemId, Long modifierId) {

    }

    private OrderSearchCriteriaDo mapToDo(final OrderSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new OrderSearchCriteriaDo(
                criteria.userIds(),
                criteria.createdFrom(),
                criteria.createdTo(),
                criteria.statuses(),
                priceUtil.countNoVatValue(criteria.totalFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.totalTo(), vatRate),
                criteria.totalUnit()
        );
    }

    private OrderData toOrderData(final OrderDo orderDo, final BigDecimal vatRate) {
        return OrderData.builder()
                .id(orderDo.getId())
                .creator(toOrderUser(userUtilService.getUserDo(orderDo.getUserId())))
                .created(orderDo.getCreated())
                .status(orderDo.getStatus())
                .orderNumber(orderDo.getOrderNumber())
                .weightValue(orderDo.getWeightValue())
                .weightUnit(orderDo.getWeightUnit())
                .netWeightValue(orderDo.getNetWeightValue())
                .netWeightUnit(orderDo.getNetWeightUnit())
                .totalValue(orderDo.getTotalValue())
                .vatTotalValue(priceUtil.countVatValue(orderDo.getTotalValue(), vatRate))
                .totalUnit(orderDo.getTotalUnit())
                .build();
    }

    private OrderUserData toOrderUser(final UserDo userDo) {
        return OrderUserData.builder()
                .id(userDo.getId())
                .titleBefore(userDo.getTitleBefore())
                .firstName(userDo.getFirstName())
                .midName(userDo.getMidName())
                .lastName(userDo.getLastName())
                .titleAfter(userDo.getTitleAfter())
                .email(userDo.getEmail())
                .build();
    }

    private OrderDo getOrderDo(final Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }
}
