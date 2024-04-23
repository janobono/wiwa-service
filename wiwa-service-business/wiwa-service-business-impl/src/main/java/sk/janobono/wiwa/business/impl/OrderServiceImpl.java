package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.OrderAttributeUtil;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.impl.util.OrderCsvUtilService;
import sk.janobono.wiwa.business.impl.util.OrderPdfUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.application.FreeDayData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderAttributeKey;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final PriceUtil priceUtil;
    private final OrderAttributeUtil orderAttributeUtil;

    private final OrderRepository orderRepository;
    private final OrderAttributeRepository orderAttributeRepository;
    private final OrderCommentRepository orderCommentRepository;
    private final OrderContactRepository orderContactRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderNumberRepository orderNumberRepository;
    private final OrderViewRepository orderViewRepository;

    private final OrderCsvUtilService orderCsvUtilService;
    private final OrderPdfUtilService orderPdfUtilService;
    private final UserUtilService userUtilService;

    private final ApplicationPropertyService applicationPropertyService;

    @Override
    public Page<OrderData> getOrders(final OrderSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return orderViewRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toOrderData(value, vatRate));
    }

    @Override
    public Page<OrderContactData> getOrderContacts(final long userId, final Pageable pageable) {
        return orderContactRepository.findAllByUserId(userId, pageable).map(value -> OrderContactData.builder()
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
    public OrderContactData setOrderContact(final long id, final OrderContactData orderContact) {
        final OrderDo order = getOrderDo(id);
        return toOrderContactData(orderContactRepository.save(OrderContactDo.builder()
                .orderId(order.getId())
                .name(orderContact.name())
                .street(orderContact.street())
                .zipCode(orderContact.street())
                .city(orderContact.city())
                .state(orderContact.state())
                .phone(orderContact.phone())
                .email(orderContact.email())
                .businessId(orderContact.businessId())
                .taxId(orderContact.taxId())
                .build()));
    }

    @Override
    public OrderData getOrder(final long id) {
        return toOrderData(getOrderViewDo(id), applicationPropertyService.getVatRate());
    }

    @Override
    public OrderData addOrder(final long userId) {
        final OrderDo orderDo = orderRepository.save(OrderDo.builder()
                .userId(userId)
                .created(LocalDateTime.now())
                .orderNumber(orderNumberRepository.getNextOrderNumber(userId))
                .build());
        return getOrder(orderDo.getId());
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
    public OrderData recountOrder(final long id, final Long modifierId) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);
        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        // TODO

        return null;
    }

    @Override
    public byte[] getPdf(final long id) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final Path pdf = orderPdfUtilService.generatePdf(orderViewDo);
        try {
            return Files.readAllBytes(pdf);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (pdf != null) {
                pdf.toFile().delete();
            }
        }
    }

    @Override
    public byte[] getCsv(final long id) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final Path csv = orderCsvUtilService.generateCsv(orderViewDo);
        try {
            return Files.readAllBytes(csv);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (csv != null) {
                csv.toFile().delete();
            }
        }
    }

    @Override
    public OrderData sendOrder(final long id, final long modifierId, final SendOrderData sendOrder) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(orderViewDo, Set.of(OrderStatus.SENT, OrderStatus.IN_PRODUCTION, OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        if (!sendOrder.businessConditionsAgreement() || !sendOrder.gdprAgreement()) {
            throw WiwaException.ORDER_AGREEMENTS_INVALID.exception("Both business conditions [{0}] and gdpr [{1}] agreements are needed",
                    sendOrder.businessConditionsAgreement(),
                    sendOrder.gdprAgreement());
        }

        checkDeliveryDate(sendOrder.deliveryDate(), applicationPropertyService.getFreeDays());

        orderContactRepository.save(OrderContactDo.builder()
                .orderId(id)
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

        // TODO


        return getOrder(id);
    }

    @Override
    public OrderData setOrderStatus(final long id, final long modifierId, final OrderStatusChangeData orderStatusChange) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(orderViewDo, Set.of(OrderStatus.NEW, OrderStatus.FINISHED, OrderStatus.CANCELLED));

        if (orderStatusChange.newStatus() == OrderStatus.NEW) {
            throw WiwaException.ORDER_STATUS_INVALID.exception("Status can't be changed to {0}",
                    orderStatusChange.newStatus());
        }

        if (orderStatusChange.notifyUser()) {
            switch (orderStatusChange.newStatus()) {
                case IN_PRODUCTION:

                    break;
                case READY:

                    break;
                case CANCELLED:

                    break;
            }
        }

        return getOrder(id);
    }

    @Override
    public List<OrderCommentData> getComments(final long id) {
        return orderCommentRepository.findAllByOrderId(id).stream()
                .map(this::toOrderCommentData)
                .toList();
    }

    @Override
    public List<OrderCommentData> addComment(final long id, final long creatorId, final OrderCommentChangeData orderCommentChange) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        orderCommentRepository.save(OrderCommentDo.builder()
                .orderId(id)
                .userId(creatorId)
                .created(LocalDateTime.now())
                .comment(orderCommentChange.comment())
                .build());

        // TODO send notification emails

        return getComments(id);
    }

    @Override
    public List<OrderItemData> getOrderItems(final long id) {
        return orderItemRepository.findAllByOrderId(id).stream()
                .map(this::toOrderItemData)
                .toList();
    }

    @Override
    public OrderItemData addItem(final long id, final long creatorId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo creator = userUtilService.getUserDo(creatorId);

        checkOrderStatus(creatorId, manager, orderViewDo);

        // TODO

        return null;
    }

    @Override
    public OrderItemData setItem(final long id, final long itemId, final long modifierId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, orderViewDo);

        // TODO

        return null;
    }

    @Override
    public OrderItemData moveUpItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, orderViewDo);

        // TODO

        return null;
    }

    @Override
    public OrderItemData moveDownItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, orderViewDo);

        // TODO

        return null;
    }

    @Override
    public void deleteItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(modifierId, manager, orderViewDo);
        orderItemRepository.deleteById(id);
    }

    private OrderViewSearchCriteriaDo mapToDo(final OrderSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new OrderViewSearchCriteriaDo(
                criteria.userIds(),
                criteria.createdFrom(),
                criteria.createdTo(),
                criteria.deliveryFrom(),
                criteria.deliveryTo(),
                criteria.statuses(),
                priceUtil.countNoVatValue(criteria.totalFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.totalTo(), vatRate)
        );
    }

    private OrderData toOrderData(final OrderDo orderDo, final BigDecimal vatRate) {
        return OrderData.builder()
//                .id(orderDo.getId())
//                .creator(toOrderUserData(userUtilService.getUserDo(orderDo.getUserId())))
//                .created(orderDo.getCreated())
//                .status(orderDo.getStatus())
//                .orderNumber(orderDo.getOrderNumber())
//                .netWeight(orderDo.getNetWeight())
//                .total(orderDo.getTotal())
//                .vatTotal(priceUtil.countVatValue(orderDo.getTotal(), vatRate))
//                .deliveryDate(orderDo.getDeliveryDate())
//                .ready(orderDo.getReady())
//                .finished(orderDo.getFinished())
                .build();
    }

    private OrderData toOrderData(final OrderViewDo orderViewDo, final BigDecimal vatRate) {
        return OrderData.builder()
                .id(orderViewDo.id())
                .creator(toOrderUserData(userUtilService.getUserDo(orderViewDo.userId())))
                .created(orderViewDo.created())
                .status(orderViewDo.status())
                .orderNumber(orderViewDo.orderNumber())
                .netWeight(new Quantity(orderViewDo.netWeight(), Unit.KILOGRAM))
//                .total(new Money(orderViewDo.total(), Unit.EUR))
//                .vatTotal(new Money(priceUtil.countVatValue(orderViewDo.total(), vatRate), Unit.EUR))
                .deliveryDate(orderViewDo.delivery())
                .build();
    }

    private OrderUserData toOrderUserData(final UserDo userDo) {
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

    private OrderCommentData toOrderCommentData(final OrderCommentDo orderCommentDo) {
        return OrderCommentData.builder()
                .id(orderCommentDo.getId())
                .creator(toOrderUserData(userUtilService.getUserDo(orderCommentDo.getId())))
                .created(orderCommentDo.getCreated())
                .comment(orderCommentDo.getComment())
                .build();
    }

    private OrderContactData toOrderContactData(final OrderContactDo orderContactDo) {
        return OrderContactData.builder()
                .name(orderContactDo.getName())
                .street(orderContactDo.getStreet())
                .zipCode(orderContactDo.getZipCode())
                .city(orderContactDo.getCity())
                .state(orderContactDo.getState())
                .phone(orderContactDo.getPhone())
                .email(orderContactDo.getEmail())
                .businessId(orderContactDo.getBusinessId())
                .taxId(orderContactDo.getTaxId())
                .build();
    }

    private OrderItemData toOrderItemData(final OrderItemDo orderItemDo) {
        return OrderItemData.builder()
                // TODO

                .build();
    }

    private OrderDo getOrderDo(final Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    private OrderViewDo getOrderViewDo(final Long id) {
        return orderViewRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    private void checkOrderStatus(final long userId, final boolean manager, final OrderViewDo orderViewDo) {
        if (manager && userId != orderViewDo.userId()) {
            checkOrderStatus(orderViewDo, Set.of(
                    OrderStatus.NEW,
                    OrderStatus.READY,
                    OrderStatus.CANCELLED,
                    OrderStatus.FINISHED));
        } else {
            checkOrderStatus(orderViewDo, Set.of(
                    OrderStatus.SENT,
                    OrderStatus.IN_PRODUCTION,
                    OrderStatus.READY,
                    OrderStatus.CANCELLED,
                    OrderStatus.FINISHED));
        }
    }

    private void checkOrderStatus(final OrderViewDo orderViewDo, final Set<OrderStatus> statuses) {
        if (statuses.contains(orderViewDo.status())) {
            throw WiwaException.ORDER_IS_IMMUTABLE.exception("Order with id {0} has status {1} is immutable",
                    orderViewDo.id(),
                    orderViewDo.status());
        }
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
