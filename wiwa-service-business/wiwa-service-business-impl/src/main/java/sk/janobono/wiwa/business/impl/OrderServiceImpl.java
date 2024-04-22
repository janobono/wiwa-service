package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.*;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.application.FreeDayData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderAttributeRepository;
import sk.janobono.wiwa.dal.repository.OrderContactRepository;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final ApplicationContext applicationContext;

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
    public Page<OrderContactData> getOrderContacts(final long userId, final Pageable pageable) {
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
    public OrderData getOrder(final long id) {
        return toOrderData(getOrderDo(id), applicationPropertyService.getVatRate());
    }

    @Override
    public OrderData addOrder(final long userId) {
        final OrderDo orderDo = orderRepository.save(
                OrderDo.builder()
                        .userId(userId)
                        .created(LocalDateTime.now())
                        .status(OrderStatus.NEW)
                        .orderNumber(orderNumberRepository.getNextOrderNumber(userId))
                        .netWeight(new Quantity(BigDecimal.ZERO, Unit.KILOGRAM))
                        .total(new Money(BigDecimal.ZERO, Unit.EUR))
                        .build());

        return toOrderData(orderDo, applicationPropertyService.getVatRate());
    }

    @Override
    public void deleteOrder(final long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Long getOrderCreatorId(final long id) {
        return orderRepository.getOrderUserId(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    @Override
    public OrderSummaryData getOrderSummary(final long id) {
        return orderAttributeRepository.findByOrderIdAndAttributeKey(id, OrderAttributeKey.SUMMARY)
                .map(orderAttributeDo -> orderAttributeUtil.parseValue(orderAttributeDo, OrderSummaryData.class))
                .orElse(OrderSummaryData.builder().build());
    }

    @Override
    public OrderData recountOrder(long id, Long modifierId) {
        // TODO
        return null;
    }

    @Override
    public byte[] getPdf(long id) {
        // TODO
        return new byte[0];
    }

    @Override
    public byte[] getCsv(long id) {
        // TODO
        return new byte[0];
    }

    @Override
    public OrderData sendOrder(final long id, final long modifierId, final SendOrderData sendOrder) {
        final OrderDo order = getOrderDo(id);

        checkOrderStatus(order, Set.of(OrderStatus.SENT, OrderStatus.IN_PRODUCTION, OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        if (!sendOrder.businessConditionsAgreement() || !sendOrder.gdprAgreement()) {
            throw WiwaException.ORDER_AGREEMENTS_INVALID.exception("Both business conditions [{0}] and gdpr [{1}] agreements are needed",
                    sendOrder.businessConditionsAgreement(),
                    sendOrder.gdprAgreement());
        }

        checkDeliveryDate(sendOrder.deliveryDate(), applicationPropertyService.getFreeDays());

        orderContactRepository.save(OrderContactDo.builder()
                .orderId(order.getId())
                .name(sendOrder.contact().name())
                .street(sendOrder.contact().street())
                .zipCode(sendOrder.contact().street())
                .city(sendOrder.contact().city())
                .state(sendOrder.contact().state())
                .phone(sendOrder.contact().phone())
                .email(sendOrder.contact().email())
                .businessId(sendOrder.contact().businessId())
                .taxId(sendOrder.contact().taxId())
                .build());

        order.setStatus(OrderStatus.SENT);
        order.setDeliveryDate(sendOrder.deliveryDate());

        final List<OrderCommentData> orderComments;
        if (Optional.ofNullable(sendOrder.comment()).map(s -> !s.isBlank()).orElse(false)) {
            final UserDo modifier = userUtilService.getUserDo(modifierId);
            orderComments = orderCommentUtil.addComment(getComments(id), toOrderUser(modifier), null, sendOrder.comment());
            orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.COMMENTS, orderComments));
        } else {
            orderComments = getComments(id);
        }

        final OrderPdfUtil orderPdfUtil = applicationContext.getBean(OrderPdfUtil.class);
        orderPdfUtil.init();
        try {
            final Path pdf = orderPdfUtil.createPdf(
                    toOrderData(order, applicationPropertyService.getVatRate()),
                    orderComments,
                    getOrderItems(id),
                    getOrderSummary(id),
                    sendOrder);

            // TODO send email with PDF

        } finally {
            orderPdfUtil.cleanUp();
        }
        return toOrderData(orderRepository.save(order), applicationPropertyService.getVatRate());
    }

    @Override
    public OrderData setOrderStatus(final long id, final long modifierId, final OrderStatusChangeData orderStatusChange) {
        final OrderDo order = getOrderDo(id);

        checkOrderStatus(order, Set.of(OrderStatus.NEW, OrderStatus.FINISHED, OrderStatus.CANCELLED));

        if (orderStatusChange.newStatus() == OrderStatus.NEW) {
            throw WiwaException.ORDER_STATUS_INVALID.exception("Status can't be changed to {0}",
                    orderStatusChange.newStatus());
        }

        // TODO


        order.setStatus(orderStatusChange.newStatus());
        return toOrderData(orderRepository.save(order), applicationPropertyService.getVatRate());
    }

    @Override
    public List<OrderCommentData> getComments(final long id) {
        return orderAttributeRepository.findByOrderIdAndAttributeKey(id, OrderAttributeKey.COMMENTS)
                .map(orderAttributeDo -> Arrays.asList(orderAttributeUtil.parseValue(orderAttributeDo, OrderCommentData[].class)))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<OrderCommentData> addComment(final long id, final long creatorId, final OrderCommentChangeData orderCommentChange) {
        final OrderDo order = getOrderDo(id);
        final UserDo creator = userUtilService.getUserDo(creatorId);
        checkOrderStatus(order, Set.of(OrderStatus.IN_PRODUCTION, OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        final List<OrderCommentData> orderComments = orderCommentUtil.addComment(
                getComments(id), toOrderUser(creator), orderCommentChange.parentId(), orderCommentChange.comment()
        );

        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.COMMENTS, orderComments));
        return orderComments;
    }

    @Override
    public List<OrderItemData> getOrderItems(final long id) {
        return orderAttributeRepository.findByOrderIdAndAttributeKey(id, OrderAttributeKey.ITEMS)
                .map(orderAttributeDo -> Arrays.asList(orderAttributeUtil.parseValue(orderAttributeDo, OrderItemData[].class)))
                .orElse(Collections.emptyList());
    }

    @Override
    public OrderItemData addItem(final long id, final long creatorId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderDo order = getOrderDo(id);
        final UserDo creator = userUtilService.getUserDo(creatorId);

        checkOrderStatus(creatorId, manager, order);

        final OrderItemUtil orderItemUtil = initOrderItemUtil(order);
        final OrderItemData orderItemData = orderItemUtil.addItem(toOrderUser(creator), orderItemChange);

        saveOrder(order, orderItemUtil);
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));

        return orderItemData;
    }

    @Override
    public OrderItemData setItem(final long id, final long itemId, final long modifierId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderDo order = getOrderDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, order);

        final OrderItemUtil orderItemUtil = initOrderItemUtil(order);
        final OrderItemData orderItemData = orderItemUtil.setItem(itemId, toOrderUser(modifier), orderItemChange);

        saveOrder(order, orderItemUtil);
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));

        return orderItemData;
    }

    @Override
    public OrderItemData moveUpItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderDo order = getOrderDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, order);

        final OrderItemUtil orderItemUtil = initOrderItemUtil(order);
        final OrderItemData orderItemData = orderItemUtil.moveUpItem(itemId, toOrderUser(modifier));

        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        return orderItemData;
    }

    @Override
    public OrderItemData moveDownItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderDo order = getOrderDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, order);

        final OrderItemUtil orderItemUtil = initOrderItemUtil(order);
        final OrderItemData orderItemData = orderItemUtil.moveDownItem(itemId, toOrderUser(modifier));

        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        return orderItemData;
    }

    @Override
    public void deleteItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderDo order = getOrderDo(id);

        checkOrderStatus(modifierId, manager, order);

        final OrderItemUtil orderItemUtil = initOrderItemUtil(order);
        orderItemUtil.deleteItem(itemId);

        saveOrder(order, orderItemUtil);
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));
    }

    private OrderSearchCriteriaDo mapToDo(final OrderSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new OrderSearchCriteriaDo(
                criteria.userIds(),
                criteria.createdFrom(),
                criteria.createdTo(),
                criteria.statuses(),
                priceUtil.countNoVatValue(criteria.totalFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.totalTo(), vatRate)
        );
    }

    private OrderData toOrderData(final OrderDo orderDo, final BigDecimal vatRate) {
        return OrderData.builder()
                .id(orderDo.getId())
                .creator(toOrderUser(userUtilService.getUserDo(orderDo.getUserId())))
                .created(orderDo.getCreated())
                .status(orderDo.getStatus())
                .orderNumber(orderDo.getOrderNumber())
                .netWeight(orderDo.getNetWeight())
                .total(orderDo.getTotal())
                .vatTotal(priceUtil.countVatValue(orderDo.getTotal(), vatRate))
                .deliveryDate(orderDo.getDeliveryDate())
                .ready(orderDo.getReady())
                .finished(orderDo.getFinished())
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

    private void checkOrderStatus(final long userId, final boolean manager, final OrderDo order) {
        if (manager && userId != order.getUserId()) {
            checkOrderStatus(order, Set.of(
                    OrderStatus.NEW,
                    OrderStatus.READY,
                    OrderStatus.CANCELLED,
                    OrderStatus.FINISHED));
        } else {
            checkOrderStatus(order, Set.of(
                    OrderStatus.SENT,
                    OrderStatus.IN_PRODUCTION,
                    OrderStatus.READY,
                    OrderStatus.CANCELLED,
                    OrderStatus.FINISHED));
        }
    }

    private void checkOrderStatus(final OrderDo order, final Set<OrderStatus> statuses) {
        if (statuses.contains(order.getStatus())) {
            throw WiwaException.ORDER_IS_IMMUTABLE.exception("Order with id {0} has status {1} is immutable",
                    order.getId(),
                    order.getStatus());
        }
    }

    private OrderItemUtil initOrderItemUtil(final OrderDo orderDo) {
        final OrderItemUtil orderItemUtil = applicationContext.getBean(OrderItemUtil.class);
        orderItemUtil.setVatRate(applicationPropertyService.getVatRate());
        orderItemUtil.setManufactureProperties(applicationPropertyService.getManufactureProperties());
        orderItemUtil.setPriceForGluingLayer(applicationPropertyService.getPriceForGluingLayer());
        orderItemUtil.setPricesForCutting(applicationPropertyService.getPricesForCutting());
        orderItemUtil.setPricesForGluingEdge(applicationPropertyService.getPricesForGluingEdge());
        orderItemUtil.setOrderItems(getOrderItems(orderDo.getId()));
        return orderItemUtil;
    }

    private void saveOrder(final OrderDo order, final OrderItemUtil orderItemUtil) {
        order.setNetWeight(orderItemUtil.getNetWeight());
        order.setTotal(orderItemUtil.getTotal());
        orderRepository.save(order);
    }

    private void checkDeliveryDate(final LocalDate deliveryDate, final List<FreeDayData> freeDays) {
        if (deliveryDate != null) {
            final boolean isFreeDay = freeDays.stream()
                    .anyMatch(freeDayData -> {
                        final int day = deliveryDate.getDayOfMonth();
                        final int month = deliveryDate.getMonth().getValue();
                        return freeDayData.day() == day && freeDayData.month() == month;
                    });
            if (isFreeDay || deliveryDate.isBefore(LocalDate.now())) {
                throw WiwaException.ORDER_DELIVERY_DATE_INVALID.exception("Order delivery date is invalid {0}", deliveryDate.toString());
            }
        }
    }
}
