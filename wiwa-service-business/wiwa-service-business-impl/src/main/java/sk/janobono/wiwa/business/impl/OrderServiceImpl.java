package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.MaterialUtil;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.impl.component.SummaryUtil;
import sk.janobono.wiwa.business.impl.component.image.BaseImageUtil;
import sk.janobono.wiwa.business.impl.component.part.PartBasicUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedBasicUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedFrameUtil;
import sk.janobono.wiwa.business.impl.component.part.PartFrameUtil;
import sk.janobono.wiwa.business.impl.model.mail.MailContentData;
import sk.janobono.wiwa.business.impl.model.mail.MailData;
import sk.janobono.wiwa.business.impl.model.mail.MailLinkData;
import sk.janobono.wiwa.business.impl.util.MailUtilService;
import sk.janobono.wiwa.business.impl.util.OrderCsvUtilService;
import sk.janobono.wiwa.business.impl.util.OrderHtmlUtilService;
import sk.janobono.wiwa.business.impl.util.UserUtilService;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.OrderService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderStatus;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.time.DayOfWeek;
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
    private final PriceUtil priceUtil;
    private final SummaryUtil summaryUtil;

    private final BoardRepository boardRepository;
    private final EdgeRepository edgeRepository;
    private final OrderRepository orderRepository;
    private final OrderCommentRepository orderCommentRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemSummaryRepository orderItemSummaryRepository;
    private final OrderMaterialRepository orderMaterialRepository;
    private final OrderNumberRepository orderNumberRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderSummaryViewRepository orderSummaryViewRepository;
    private final OrderViewRepository orderViewRepository;
    private final UserRepository userRepository;

    private final MailUtilService mailUtilService;
    private final OrderCsvUtilService orderCsvUtilService;
    private final OrderHtmlUtilService orderHtmlUtilService;
    private final UserUtilService userUtilService;

    private final ApplicationPropertyService applicationPropertyService;

    @Override
    public Page<OrderData> getOrders(final OrderSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return orderViewRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toOrderData(value, vatRate));
    }

    @Override
    public Page<OrderUserData> getOrderUsers(final OrderUserSearchCriteriaData criteria, final Pageable pageable) {
        return userRepository.findAll(UserSearchCriteriaDo.builder()
                        .searchField(criteria.searchField())
                        .email(criteria.email())
                        .build(), pageable)
                .map(this::toOrderUserData);
    }

    @Override
    public OrderContactData getLastOrderContact(final long userId) {
        final OrderViewSearchCriteriaDo criteria = OrderViewSearchCriteriaDo.builder()
                .userIds(Set.of(userId))
                .statuses(Set.of(OrderStatus.SENT, OrderStatus.IN_PRODUCTION, OrderStatus.READY, OrderStatus.FINISHED))
                .build();
        final Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"));

        return orderViewRepository.findAll(criteria, pageable).stream()
                .map(OrderViewDo::contact)
                .map(contact -> dataUtil.parseValue(contact, OrderContactData.class))
                .findFirst()
                .orElseGet(() -> OrderContactData.builder().build());
    }

    @Transactional
    @Override
    public OrderData setOrderContact(final long id, final OrderContactData orderContact) {
        final OrderDo order = getOrderDo(id);
        orderRepository.setContact(order.getId(), dataUtil.serializeValue(orderContact));
        return getOrder(id);
    }

    @Override
    public OrderData getOrder(final long id) {
        return toOrderData(getOrderViewDo(id), applicationPropertyService.getVatRate());
    }

    @Transactional
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

    @Transactional
    @Override
    public void deleteOrder(final long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Long getOrderCreatorId(final long id) {
        return orderRepository.getOrderUserId(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }

    @Transactional
    @Override
    public OrderData recountOrder(final long id, final Long modifierId) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        checkOrderStatus(orderViewDo, Set.of(OrderStatus.READY, OrderStatus.CANCELLED, OrderStatus.FINISHED));
        recountOrderItems(id);
        recountOrderSummary(id);
        return toOrderData(getOrderViewDo(id), applicationPropertyService.getVatRate());
    }

    @Override
    public String getHtml(final long id) {
        return orderHtmlUtilService.generateHtml(getOrder(id));
    }

    @Override
    public String getCsv(final long id) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);
        return orderCsvUtilService.generateCsv(orderViewDo);
    }

    @Transactional
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

        orderRepository.setContact(id, dataUtil.serializeValue(sendOrder.contact()));
        orderRepository.setDelivery(id, sendOrder.deliveryDate());
        orderRepository.setPackageType(id, sendOrder.packageType());

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(id)
                .userId(modifierId)
                .created(LocalDateTime.now())
                .status(OrderStatus.SENT)
                .build());

        final OrderData order = getOrder(id);

        final OrderSendMailData orderSendMail = applicationPropertyService.getOrderSendMail();
        mailUtilService.sendEmail(MailData.builder()
                .from(commonConfigProperties.mail())
                .recipients(getEmails(owner, sendOrder.contact()))
                .cc(List.of(commonConfigProperties.ordersMail()))
                .subject(MessageFormat.format(orderSendMail.subject(), orderViewDo.orderNumber()))
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
                        toHtmlFile(orderHtmlUtilService.generateHtml(order))
                ))
                .build());

        return order;
    }

    @Transactional
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
                .recipients(getEmails(
                        owner,
                        Optional.ofNullable(orderViewDo.contact())
                                .map(contact -> dataUtil.parseValue(contact, OrderContactData.class))
                                .orElse(null)
                ))
                .cc(List.of(commonConfigProperties.ordersMail()));

        final MailLinkData mailLink = MailLinkData.builder()
                .href(getOrderUrl(id))
                .text(orderStatusMail.link())
                .build();

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(id)
                .userId(modifierId)
                .created(LocalDateTime.now())
                .status(orderStatusChange.newStatus())
                .build());

        final OrderData order = getOrder(id);

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
                    mailDataBuilder.subject(MessageFormat.format(orderStatusMail.readySubject(), orderViewDo.orderNumber()));
                    mailDataBuilder.content(MailContentData.builder()
                            .title(MessageFormat.format(orderStatusMail.readyTitle(), orderViewDo.orderNumber()))
                            .lines(List.of(orderStatusMail.readyMessage()))
                            .mailLink(mailLink)
                            .build());
                    mailDataBuilder.attachments(Map.of(
                            MessageFormat.format(orderStatusMail.attachment(), orderViewDo.orderNumber()),
                            toHtmlFile(orderHtmlUtilService.generateHtml(order))
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

        return order;
    }

    @Transactional
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
                .recipients(getEmails(
                        owner,
                        Optional.ofNullable(orderViewDo.contact())
                                .map(contact -> dataUtil.parseValue(contact, OrderContactData.class))
                                .orElse(null)
                ))
                .cc(List.of(commonConfigProperties.ordersMail()))
                .subject(MessageFormat.format(orderCommentMail.subject(), orderViewDo.orderNumber()))
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

    @Transactional
    @Override
    public OrderData addItem(final long id, final long creatorId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(creatorId, manager, orderViewDo);

        validate(id, orderItemChange);

        final OrderItemDo orderItemDo = orderItemRepository.insert(OrderItemDo.builder()
                .orderId(id)
                .sortNum(orderItemRepository.countByOrderId(id))
                .name(orderItemChange.name())
                .description(orderItemChange.description())
                .quantity(orderItemChange.quantity())
                .part(dataUtil.serializeValue(orderItemChange.part()))
                .build());

        recountItemSummary(id, orderItemDo.getId());

        recountOrderSummary(id);

        return getOrder(id);
    }

    @Transactional
    @Override
    public OrderData setItem(final long id, final long itemId, final long modifierId, final OrderItemChangeData orderItemChange, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        setItem(id, itemId, orderItemChange);

        recountOrderSummary(id);

        return getOrder(id);
    }

    @Transactional
    @Override
    public OrderData moveUpItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        sortItems(id);

        final OrderItemDo item = getOrderItemDo(itemId);
        if (item.getSortNum() > 0) {
            final OrderItemDo upItem = getOrderItemDo(id, item.getSortNum() - 1);
            orderItemRepository.setSortNum(upItem.getId(), item.getSortNum());
            orderItemRepository.setSortNum(item.getId(), upItem.getSortNum());
        }

        return getOrder(id);
    }

    @Transactional
    @Override
    public OrderData moveDownItem(final long id, final long itemId, final long modifierId, final boolean manager) {
        final OrderViewDo orderViewDo = getOrderViewDo(id);

        checkOrderStatus(modifierId, manager, orderViewDo);

        sortItems(id);

        final OrderItemDo item = getOrderItemDo(itemId);
        if (item.getSortNum() < orderItemRepository.countByOrderId(id)) {
            final OrderItemDo downItem = getOrderItemDo(id, item.getSortNum() + 1);
            orderItemRepository.setSortNum(downItem.getId(), item.getSortNum());
            orderItemRepository.setSortNum(item.getId(), downItem.getSortNum());
        }

        return getOrder(id);
    }

    @Override
    public List<OrderItemImageData> getItemImages(final long id, final long itemId) {
        final OrderItemDo orderItemDo = getOrderItemDo(itemId);
        final PartData part = dataUtil.parseValue(orderItemDo.getPart(), PartData.class);
        final OrderPropertiesData orderProperties = applicationPropertyService.getOrderProperties();
        return BaseImageUtil.partImages(orderProperties, part);
    }

    @Transactional
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
                .contact(Optional.ofNullable(orderViewDo.contact())
                        .map(contact -> dataUtil.parseValue(contact, OrderContactData.class))
                        .orElse(null)
                )
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
                .creator(toOrderUserData(userUtilService.getUserDo(orderCommentDo.getUserId())))
                .created(orderCommentDo.getCreated())
                .comment(orderCommentDo.getComment())
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
            if (deliveryDate.getDayOfWeek() == DayOfWeek.SATURDAY || deliveryDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw WiwaException.ORDER_DELIVERY_DATE_INVALID.exception("Order delivery date is invalid {0}", deliveryDate.toString());
            }
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
        for (final OrderItemDo item : items) {
            orderItemRepository.setSortNum(item.getId(), sortNum);
            sortNum++;
        }
    }

    private List<String> getEmails(final UserDo owner, final OrderContactData orderContact) {
        final List<String> emails = new ArrayList<>();
        emails.add(owner.getEmail());
        Optional.ofNullable(orderContact).map(OrderContactData::email).ifPresent(email -> {
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

        switch (orderItemChange.part()) {
            case final PartBasicData partBasicData ->
                    new PartBasicUtil().validate(partBasicData, boards, edges, applicationPropertyService.getManufactureProperties());
            case final PartFrameData partFrameData ->
                    new PartFrameUtil().validate(partFrameData, boards, edges, applicationPropertyService.getManufactureProperties());
            case final PartDuplicatedBasicData partDuplicatedBasicData ->
                    new PartDuplicatedBasicUtil().validate(partDuplicatedBasicData, boards, edges, applicationPropertyService.getManufactureProperties());
            case final PartDuplicatedFrameData partDuplicatedFrameData ->
                    new PartDuplicatedFrameUtil().validate(partDuplicatedFrameData, boards, edges, applicationPropertyService.getManufactureProperties());
            default ->
                    throw new InvalidParameterException("Unsupported part type: " + orderItemChange.part().getClass().getSimpleName());
        }

        for (final OrderBoardData board : boards.values()) {
            orderMaterialRepository.save(materialUtil.toMaterial(orderId, board));
        }

        for (final OrderEdgeData edge : edges.values()) {
            orderMaterialRepository.save(materialUtil.toMaterial(orderId, edge));
        }
    }

    private void setItem(final long id, final long itemId, final OrderItemChangeData orderItemChange) {
        validate(id, orderItemChange);

        orderItemRepository.setName(itemId, orderItemChange.name());
        orderItemRepository.setDescription(itemId, orderItemChange.description());
        orderItemRepository.setQuantity(itemId, orderItemChange.quantity());
        orderItemRepository.setPart(itemId, dataUtil.serializeValue(orderItemChange.part()));

        recountItemSummary(id, itemId);
    }

    private void recountItemSummary(final long id, final long itemId) {
        final OrderItemDo orderItem = getOrderItemDo(itemId);

        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(id);

        final OrderItemSummaryData orderItemSummary = summaryUtil.calculateItemSummary(
                dataUtil.parseValue(orderItem.getPart(), PartData.class),
                orderItem.getQuantity(),
                materialUtil.toBoards(materials).stream().collect(Collectors.toMap(OrderBoardData::id, OrderBoardData::thickness)),
                applicationPropertyService.getManufactureProperties()
        );

        final List<OrderItemSummaryDo> orderItemSummaryList = summaryUtil.toOrderItemSummaries(itemId, orderItemSummary);
        orderItemSummaryRepository.deleteByOrderItemId(itemId);
        for (final OrderItemSummaryDo orderItemSummaryDo : orderItemSummaryList) {
            orderItemSummaryRepository.insert(orderItemSummaryDo);
        }
    }

    private void recountOrderItems(final long id) {
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(id);
        for (final OrderItemDo item : items) {
            setItem(id, item.getId(), new OrderItemChangeData(
                    item.getName(),
                    item.getDescription(),
                    item.getQuantity(),
                    dataUtil.parseValue(item.getPart(), PartData.class)
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

        orderRepository.setWeight(id, orderSummary.weight());
        orderRepository.setTotal(id, orderSummary.total());
        orderRepository.setSummary(id, dataUtil.serializeValue(orderSummary));
    }

    private File toHtmlFile(final String html) {
        try {
            final Path path = Files.createTempFile("Order", ".html");
            Files.write(path, html.getBytes());
            return path.toFile();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
