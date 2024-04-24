package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.impl.model.mail.MailContentData;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.model.mail.MailLinkData;
import sk.janobono.wiwa.business.impl.model.mail.MailTemplate;
import sk.janobono.wiwa.business.impl.model.order.OrderItemJson;
import sk.janobono.wiwa.business.impl.model.order.OrderJson;
import sk.janobono.wiwa.business.impl.util.MailUtilService;
import sk.janobono.wiwa.business.impl.util.OrderCsvUtilService;
import sk.janobono.wiwa.business.impl.util.OrderPdfUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.application.FreeDayData;
import sk.janobono.wiwa.business.model.application.OrderCommentMailData;
import sk.janobono.wiwa.business.model.application.OrderSendMailData;
import sk.janobono.wiwa.business.model.application.OrderStatusMailData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.OderItemSortNumDo;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final CommonConfigProperties commonConfigProperties;

    private final DataUtil dataUtil;
    private final PriceUtil priceUtil;

    private final OrderRepository orderRepository;
    private final OrderCommentRepository orderCommentRepository;
    private final OrderContactRepository orderContactRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderNumberRepository orderNumberRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderViewRepository orderViewRepository;

    private final MailUtilService mailUtilService;
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
        final OrderDo orderDo = orderRepository.insert(OrderDo.builder()
                .userId(userId)
                .created(LocalDateTime.now())
                .orderNumber(orderNumberRepository.getNextOrderNumber(userId))
                .data(dataUtil.serializeValue(OrderJson.builder().build()))
                .build());
        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(orderDo.getId())
                .userId(userId)
                .created(orderDo.getCreated())
                .status(OrderStatus.NEW)
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
        return dataUtil.parseValue(getOrderDo(id).getData(), OrderJson.class).getOrderSummaryData();
    }

    @Override
    public OrderData recountOrder(final long id, final Long modifierId) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        recountItems(id, modifier);
        recountSummary(id);

        return toOrderData(getOrderViewDo(id), applicationPropertyService.getVatRate());
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
        final UserDo owner = userUtilService.getUserDo(orderViewDo.userId());

        checkOrderStatus(orderViewDo, Set.of(OrderStatus.SENT, OrderStatus.IN_PRODUCTION, OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        if (orderViewDo.total().intValue() == 0) {
            throw WiwaException.ORDER_IS_EMPTY.exception("Order is empty");
        }

        if (!sendOrder.businessConditionsAgreement() || !sendOrder.gdprAgreement()) {
            throw WiwaException.ORDER_AGREEMENTS_INVALID.exception("Both business conditions [{0}] and gdpr [{1}] agreements are needed",
                    sendOrder.businessConditionsAgreement(),
                    sendOrder.gdprAgreement());
        }

        checkDeliveryDate(sendOrder.deliveryDate(), applicationPropertyService.getFreeDays());

        final OrderContactDo orderContact = orderContactRepository.save(OrderContactDo.builder()
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

        final Path pdf = orderPdfUtilService.generatePdf(orderViewDo);

        final OrderSendMailData orderSendMail = applicationPropertyService.getOrderSendMail();
        mailUtilService.sendEmail(MailData.builder()
                .from(commonConfigProperties.mail())
                .recipients(getEmails(owner, orderContact))
                .cc(List.of(commonConfigProperties.ordersMail()))
                .subject(MessageFormat.format(orderSendMail.subject(), orderViewDo.orderNumber()))
                .template(MailTemplate.BASE)
                .content(MailContentData.builder()
                        .title(MessageFormat.format(orderSendMail.title(), orderViewDo.orderNumber()))
                        .lines(List.of(orderSendMail.message()))
                        .mailLink(MailLinkData.builder()
                                .href(getOrderUrl(id))
                                .text(orderSendMail.link())
                                .build())
                        .build())
                .attachments(Map.of(
                        MessageFormat.format(orderSendMail.attachment(), orderViewDo.orderNumber()),
                        pdf.toFile()
                ))
                .build());

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(id)
                .userId(modifierId)
                .created(LocalDateTime.now())
                .status(OrderStatus.SENT)
                .build());

        return getOrder(id);
    }

    @Override
    public OrderData setOrderStatus(final long id, final long modifierId, final OrderStatusChangeData orderStatusChange) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo owner = userUtilService.getUserDo(orderViewDo.userId());

        checkOrderStatus(orderViewDo, Set.of(OrderStatus.NEW, OrderStatus.FINISHED, OrderStatus.CANCELLED));

        if (orderStatusChange.newStatus() == OrderStatus.NEW || orderStatusChange.newStatus() == OrderStatus.SENT) {
            throw WiwaException.ORDER_STATUS_INVALID.exception("Status can't be changed to {0}",
                    orderStatusChange.newStatus());
        }

        final OrderStatusMailData orderStatusMail = applicationPropertyService.getOrderStatusMail();
        final MailData.MailDataBuilder mailDataBuilder = MailData.builder();
        mailDataBuilder.from(commonConfigProperties.mail())
                .recipients(getEmails(owner, orderContactRepository.findByOrderId(id).orElse(null)))
                .cc(List.of(commonConfigProperties.ordersMail()))
                .template(MailTemplate.BASE);

        final MailLinkData mailLink = MailLinkData.builder()
                .href(getOrderUrl(id))
                .text(orderStatusMail.link())
                .build();

        if (orderStatusChange.notifyUser()) {
            switch (orderStatusChange.newStatus()) {
                case IN_PRODUCTION:
                    mailDataBuilder.subject(MessageFormat.format(orderStatusMail.productionSubject(), orderViewDo.orderNumber()));
                    mailDataBuilder.content(MailContentData.builder()
                            .title(MessageFormat.format(orderStatusMail.productionTitle(), orderViewDo.orderNumber()))
                            .lines(List.of(orderStatusMail.productionMessage()))
                            .mailLink(mailLink)
                            .build());
                    break;
                case READY:
                    final Path pdf = orderPdfUtilService.generatePdf(orderViewDo);
                    mailDataBuilder.subject(MessageFormat.format(orderStatusMail.readySubject(), orderViewDo.orderNumber()));
                    mailDataBuilder.content(MailContentData.builder()
                            .title(MessageFormat.format(orderStatusMail.readyTitle(), orderViewDo.orderNumber()))
                            .lines(List.of(orderStatusMail.readyMessage()))
                            .mailLink(mailLink)
                            .build());
                    mailDataBuilder.attachments(Map.of(
                            MessageFormat.format(orderStatusMail.attachment(), orderViewDo.orderNumber()),
                            pdf.toFile()
                    ));
                    break;
                case FINISHED:
                    mailDataBuilder.subject(MessageFormat.format(orderStatusMail.finishedSubject(), orderViewDo.orderNumber()));
                    mailDataBuilder.content(MailContentData.builder()
                            .title(MessageFormat.format(orderStatusMail.finishedTitle(), orderViewDo.orderNumber()))
                            .lines(List.of(orderStatusMail.finishedMessage()))
                            .mailLink(mailLink)
                            .build());
                    break;
                case CANCELLED:
                    mailDataBuilder.subject(MessageFormat.format(orderStatusMail.cancelledSubject(), orderViewDo.orderNumber()));
                    mailDataBuilder.content(MailContentData.builder()
                            .title(MessageFormat.format(orderStatusMail.cancelledTitle(), orderViewDo.orderNumber()))
                            .lines(List.of(orderStatusMail.cancelledMessage()))
                            .mailLink(mailLink)
                            .build());
                    break;
            }
        }

        mailUtilService.sendEmail(mailDataBuilder.build());

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(id)
                .userId(modifierId)
                .created(LocalDateTime.now())
                .status(orderStatusChange.newStatus())
                .build());

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
        final UserDo owner = userUtilService.getUserDo(orderViewDo.userId());
        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));

        orderCommentRepository.save(OrderCommentDo.builder()
                .orderId(id)
                .userId(creatorId)
                .created(LocalDateTime.now())
                .comment(orderCommentChange.comment())
                .build());

        final OrderCommentMailData orderCommentMail = applicationPropertyService.getOrderCommentMail();
        mailUtilService.sendEmail(MailData.builder()
                .from(commonConfigProperties.mail())
                .recipients(getEmails(owner, orderContactRepository.findByOrderId(id).orElse(null)))
                .cc(List.of(commonConfigProperties.ordersMail()))
                .subject(MessageFormat.format(orderCommentMail.subject(), orderViewDo.orderNumber()))
                .template(MailTemplate.BASE)
                .content(MailContentData.builder()
                        .title(MessageFormat.format(orderCommentMail.title(), orderViewDo.orderNumber()))
                        .lines(List.of(
                                orderCommentMail.message(),
                                orderCommentChange.comment()
                        ))
                        .mailLink(MailLinkData.builder()
                                .href(getOrderUrl(id))
                                .text(orderCommentMail.link())
                                .build())
                        .build())
                .build());
        return getComments(id);
    }

    @Override
    public List<OrderItemData> getOrderItems(final long id) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return orderItemRepository.findAllByOrderId(id).stream()
                .map(v -> toOrderItemData(v, vatRate))
                .toList();
    }

    @Override
    public OrderItemData addItem(final long id, final long creatorId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo creator = userUtilService.getUserDo(creatorId);

        checkOrderStatus(creatorId, manager, orderViewDo);

        // boards

        // edges


        // TODO

        return null;
    }

    @Override
    public OrderItemData setItem(final long id, final long itemId, final long modifierId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        final UserDo modifier = userUtilService.getUserDo(modifierId);

        checkOrderStatus(modifierId, manager, orderViewDo);

        // TODO

        recountSummary(id);
        return null;
    }

    @Override
    public OrderItemData moveUpItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        sortItems(id);

        final OrderItemDo item = getOrderItemDo(itemId);
        if (item.getSortNum() > 0) {
            final OrderItemDo upItem = getOrderItemDo(id, item.getSortNum() - 1);
            orderItemRepository.setSortNums(List.of(
                    new OderItemSortNumDo(upItem.getId(), item.getSortNum()),
                    new OderItemSortNumDo(item.getId(), upItem.getSortNum())
            ));
        }

        return toOrderItemData(item, applicationPropertyService.getVatRate());
    }

    @Override
    public OrderItemData moveDownItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        sortItems(id);

        final OrderItemDo item = getOrderItemDo(itemId);
        if (item.getSortNum() < orderItemRepository.countByOrderId(id)) {
            final OrderItemDo downItem = getOrderItemDo(id, item.getSortNum() + 1);
            orderItemRepository.setSortNums(List.of(
                    new OderItemSortNumDo(downItem.getId(), item.getSortNum()),
                    new OderItemSortNumDo(item.getId(), downItem.getSortNum())
            ));
        }

        return toOrderItemData(item, applicationPropertyService.getVatRate());
    }

    @Override
    public void deleteItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(modifierId, manager, orderViewDo);
        orderItemRepository.deleteById(id);
        sortItems(id);
        recountSummary(id);
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

    private OrderData toOrderData(final OrderViewDo orderViewDo, final BigDecimal vatRate) {
        return OrderData.builder()
                .id(orderViewDo.id())
                .creator(toOrderUserData(userUtilService.getUserDo(orderViewDo.userId())))
                .created(orderViewDo.created())
                .status(orderViewDo.status())
                .orderNumber(orderViewDo.orderNumber())
                .weight(new Quantity(orderViewDo.weight(), Unit.KILOGRAM))
                .total(new Money(orderViewDo.total(), commonConfigProperties.currency()))
                .vatTotal(new Money(priceUtil.countVatValue(orderViewDo.total(), vatRate), commonConfigProperties.currency()))
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

    private OrderItemData toOrderItemData(final OrderItemDo orderItemDo, final BigDecimal vatRate) {
        return OrderItemData.builder()
                .id(orderItemDo.getId())
                .sortNum(orderItemDo.getSortNum())
                .name(orderItemDo.getName())
                .partPrice(new Money(orderItemDo.getPartPrice(), commonConfigProperties.currency()))
                .vatPartPrice(new Money(priceUtil.countVatValue(orderItemDo.getPartPrice(), vatRate), commonConfigProperties.currency()))
                .amount(new Quantity(BigDecimal.valueOf(orderItemDo.getAmount()), Unit.PIECE))
                .weight(new Quantity(orderItemDo.getWeight(), Unit.KILOGRAM))
                .total(new Money(orderItemDo.getTotal(), commonConfigProperties.currency()))
                .vatTotal(new Money(priceUtil.countVatValue(orderItemDo.getTotal(), vatRate), commonConfigProperties.currency()))
                .partData(dataUtil.parseValue(orderItemDo.getData(), OrderItemJson.class).getParData())
                .build();
    }

    private OrderDo getOrderDo(final long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    private OrderViewDo getOrderViewDo(final long id) {
        return orderViewRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    private OrderItemDo getOrderItemDo(final long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_ITEM_NOT_FOUND.exception("Order item with id {0} not found", id));
    }

    private OrderItemDo getOrderItemDo(final long orderId, final int sortNum) {
        return orderItemRepository.findByOrderIdAndSortNum(orderId, sortNum)
                .orElseThrow(() -> WiwaException.ORDER_ITEM_NOT_FOUND.exception("Order item with order id {0} and sort number {1} not found", orderId, sortNum));
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

    private String getOrderUrl(final long id) {
        return commonConfigProperties.webUrl() + commonConfigProperties.ordersPath() + id;
    }

    private void sortItems(final long id) {
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(id);
        int sortNum = 0;
        final List<OderItemSortNumDo> sortNums = new ArrayList<>();
        for (final OrderItemDo item : items) {
            sortNums.add(new OderItemSortNumDo(item.getId(), sortNum));
            sortNum++;
        }
        orderItemRepository.setSortNums(sortNums);
    }

    private List<String> getEmails(final UserDo owner, final OrderContactDo orderContact) {
        final List<String> emails = new ArrayList<>();
        emails.add(owner.getEmail());
        Optional.ofNullable(orderContact).map(OrderContactDo::getEmail).ifPresent(email -> {
            if (!emails.contains(email)) {
                emails.add(email);
            }
        });
        return emails;
    }

    private void recountItems(long id, UserDo modifier) {
        // TODO
    }

    private void recountSummary(long id) {
        // TODO
    }
}
