package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.*;
import sk.janobono.wiwa.business.impl.model.mail.MailContentData;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.model.mail.MailLinkData;
import sk.janobono.wiwa.business.impl.model.mail.MailTemplate;
import sk.janobono.wiwa.business.impl.util.MailUtilService;
import sk.janobono.wiwa.business.impl.util.OrderCsvUtilService;
import sk.janobono.wiwa.business.impl.util.OrderPdfUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.application.FreeDayData;
import sk.janobono.wiwa.business.model.application.OrderCommentMailData;
import sk.janobono.wiwa.business.model.application.OrderSendMailData;
import sk.janobono.wiwa.business.model.application.OrderStatusMailData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.*;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final CommonConfigProperties commonConfigProperties;

    private final DataUtil dataUtil;
    private final MaterialUtil materialUtil;
    private final PartUtil partUtil;
    private final PriceUtil priceUtil;
    private final SummaryUtil summaryUtil;

    private final BoardRepository boardRepository;
    private final EdgeRepository edgeRepository;
    private final OrderRepository orderRepository;
    private final OrderCommentRepository orderCommentRepository;
    private final OrderContactRepository orderContactRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemSummaryRepository orderItemSummaryRepository;
    private final OrderMaterialRepository orderMaterialRepository;
    private final OrderNumberRepository orderNumberRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderSummaryViewRepository orderSummaryViewRepository;
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
    public OrderData setOrderContact(final long id, final OrderContactData orderContact) {
        final OrderDo order = getOrderDo(id);
        orderContactRepository.save(OrderContactDo.builder()
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
                .build());
        return getOrder(id);
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
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .summary(dataUtil.serializeValue(summaryUtil.createEmptySummary()))
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
    public OrderData recountOrder(final long id, final Long modifierId) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));
        recountOrderItems(id);
        recountOrderSummary(id);
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
                final boolean deleted = pdf.toFile().delete();
                if (!deleted) {
                    log.warn("Pdf wasn't deleted {}", pdf);
                }
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
                final boolean deleted = csv.toFile().delete();
                if (!deleted) {
                    log.warn("Csv wasn't deleted {}", csv);
                }
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

        orderRepository.setDelivery(id, new OrderDeliveryDo(sendOrder.deliveryDate(), sendOrder.packageType()));

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
    public OrderData addComment(final long id, final long creatorId, final OrderCommentChangeData orderCommentChange) {
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

        return getOrder(id);
    }

    @Override
    public OrderData addItem(final long id, final long creatorId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(creatorId, manager, orderViewDo);

        validate(id, orderItemChange);

        final OrderItemDo orderItemDo = orderItemRepository.insert(OrderItemDo.builder()
                .orderId(id)
                .sortNum(orderItemRepository.countByOrderId(id))
                .name(orderItemChange.name())
                .quantity(orderItemChange.quantity())
                .part(dataUtil.serializeValue(orderItemChange.part()))
                .build());

        recountItemSummary(id, orderItemDo.getId());

        recountOrderSummary(id);

        return getOrder(id);
    }

    @Override
    public OrderData setItem(final long id, final long itemId, final long modifierId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        setItem(id, itemId, orderItemChange);

        recountOrderSummary(id);

        return getOrder(id);
    }

    @Override
    public OrderData moveUpItem(final long id, final long itemId, final long modifierId, final boolean manager) {
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

        return getOrder(id);
    }

    @Override
    public OrderData moveDownItem(final long id, final long itemId, final long modifierId, final boolean manager) {
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

        return getOrder(id);
    }

    @Override
    public OrderData deleteItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(modifierId, manager, orderViewDo);
        orderItemRepository.deleteById(id);
        sortItems(id);
        recountOrderSummary(id);
        return getOrder(id);
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
        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(orderViewDo.id());
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(orderViewDo.id());
        final List<OrderCommentDo> comments = orderCommentRepository.findAllByOrderId(orderViewDo.id());

        return OrderData.builder()
                .id(orderViewDo.id())
                .creator(toOrderUserData(userUtilService.getUserDo(orderViewDo.userId())))
                .created(orderViewDo.created())
                .status(orderViewDo.status())
                .orderNumber(orderViewDo.orderNumber())
                .weight(orderViewDo.weight())
                .total(orderViewDo.total())
                .vatTotal(priceUtil.countVatValue(orderViewDo.total(), vatRate))
                .deliveryDate(orderViewDo.delivery())
                .packageType(orderViewDo.packageType())
                .contact(orderContactRepository.findByOrderId(orderViewDo.id())
                        .map(this::toOrderContactData).orElse(null))
                .boards(materialUtil.toBoards(materials))
                .edges(materialUtil.toEdges(materials))
                .items(items.stream().map(this::toOrderItemData).toList())
                .summary(orderRepository.findById(orderViewDo.id())
                        .map(o -> dataUtil.parseValue(o.getSummary(), OrderSummaryData.class)).orElse(null))
                .comments(comments.stream().map(this::toOrderCommentData).toList())
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
                .id(orderItemDo.getId())
                .sortNum(orderItemDo.getSortNum())
                .name(orderItemDo.getName())
                .quantity(orderItemDo.getQuantity())
                .part(dataUtil.parseValue(orderItemDo.getPart(), PartData.class))
                .summary(summaryUtil.toOrderItemSummary(orderItemSummaryRepository.findAllByOrderItemId(orderItemDo.getId())))
                .build();
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

    private OrderBoardData toOrderBoard(final BoardDo boardDo) {
        return OrderBoardData.builder()
                .id(boardDo.getId())
                .code(boardDo.getCode())
                .name(boardDo.getName())
                .boardCode(boardDo.getBoardCode())
                .structureCode(boardDo.getStructureCode())
                .orientation(boardDo.getOrientation())
                .weight(boardDo.getWeight())
                .length(boardDo.getLength())
                .width(boardDo.getWidth())
                .thickness(boardDo.getThickness())
                .price(boardDo.getPrice())
                .build();
    }

    private OrderEdgeData toOrderEdge(final EdgeDo edgeDo) {
        return OrderEdgeData.builder()
                .id(edgeDo.getId())
                .code(edgeDo.getCode())
                .name(edgeDo.getName())
                .weight(edgeDo.getWeight())
                .width(edgeDo.getWidth())
                .thickness(edgeDo.getThickness())
                .price(edgeDo.getPrice())
                .build();
    }

    private void validate(final long orderId, final OrderItemChangeData orderItemChange) {
        final Set<Long> boardIds = new HashSet<>(orderItemChange.part().boards().values());
        final Map<Long, OrderBoardData> boards = new HashMap<>();
        for (final Long boardId : boardIds) {
            boards.putIfAbsent(boardId,
                    boardRepository.findById(boardId)
                            .map(this::toOrderBoard)
                            .orElseThrow(() -> WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", boardId))
            );
        }

        final Set<Long> edgeIds = new HashSet<>(orderItemChange.part().edges().values());
        final Map<Long, OrderEdgeData> edges = new HashMap<>();
        for (final Long edgeId : edgeIds) {
            edges.putIfAbsent(edgeId, edgeRepository.findById(edgeId)
                    .map(this::toOrderEdge)
                    .orElseThrow(() -> WiwaException.EDGE_NOT_FOUND.exception("Edge with id {0} not found", edgeId))
            );
        }

        partUtil.validate(orderItemChange.part(), boards, edges, applicationPropertyService.getManufactureProperties());

        for (final OrderBoardData board : boards.values()) {
            orderMaterialRepository.save(materialUtil.toMaterial(orderId, board));
        }

        for (final OrderEdgeData edge : edges.values()) {
            orderMaterialRepository.save(materialUtil.toMaterial(orderId, edge));
        }
    }

    private void setItem(final long id, final long itemId, final OrderItemChangeData orderItemChange) {
        validate(id, orderItemChange);

        orderItemRepository.setOrderItemInfo(itemId, new OrderItemInfoDo(orderItemChange.name(), orderItemChange.quantity()));
        orderItemRepository.setPart(itemId, dataUtil.serializeValue(orderItemChange.part()));

        recountItemSummary(id, itemId);
    }

    private void recountItemSummary(final Long id, final Long itemId) {
        final OrderItemDo orderItem = getOrderItemDo(itemId);

        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(id);

        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(
                dataUtil.parseValue(orderItem.getPart(), PartData.class),
                orderItem.getQuantity(),
                materialUtil.toBoards(materials).stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                applicationPropertyService.getManufactureProperties()
        );

        final List<OrderItemSummaryDo> orderItemSummaryList = summaryUtil.toOrderItemSummaries(itemId, orderItemSummary);
        orderItemSummaryRepository.saveAll(itemId, orderItemSummaryList);
    }

    private void recountOrderItems(final long id) {
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(id);
        for (final OrderItemDo item : items) {
            setItem(id, item.getId(), new OrderItemChangeData(
                    item.getName(), item.getQuantity(), dataUtil.parseValue(item.getPart(), PartData.class)
            ));
            recountItemSummary(id, item.getId());
        }
    }

    private void recountOrderSummary(final long id) {
        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(id);

        final OrderSummaryData orderSummary = summaryUtil.toOrderSummary(
                materialUtil.toBoards(materials),
                materialUtil.toEdges(materials),
                applicationPropertyService.getVatRate(),
                applicationPropertyService.getPricesForCutting(),
                applicationPropertyService.getPriceForGluingLayer(),
                applicationPropertyService.getPricesForGluingEdge(),
                orderSummaryViewRepository.findAllById(id)
        );

        orderRepository.setOrderTotal(id, new OrderTotalDo(orderSummary.weight(), orderSummary.total()));
        orderRepository.setSummary(id, dataUtil.serializeValue(orderSummary));
    }
}
