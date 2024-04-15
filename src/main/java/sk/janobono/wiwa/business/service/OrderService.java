package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.order.OrderChangeData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.component.TimeUtil;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final PriceUtil priceUtil;
    private final TimeUtil timeUtil;

    private final OrderRepository orderRepository;
    private final OrderNumberRepository orderNumberRepository;

    private final ApplicationPropertyService applicationPropertyService;

    public Page<OrderData> getOrders(final OrderSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = getVatRate();
        return orderRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toOrderData(value, vatRate));
    }

    public OrderData getOrder(final Long id) {
        return toOrderData(getOrderDo(id), getVatRate());
    }

    public OrderData addOrder(final Long userId, final OrderChangeData orderChangeData) {
        final BigDecimal vatRate = getVatRate();
        return toOrderData(orderRepository.save(
                OrderDo.builder()
                        .userId(userId)
                        .created(LocalDateTime.now())
                        .status(OrderStatus.NEW)
                        .orderNumber(orderNumberRepository.getNextOrderNumber(userId))
                        .description(orderChangeData.description())
                        .weightValue(BigDecimal.ZERO)
                        .weightUnit(Unit.KILOGRAM)
                        .netWeightValue(BigDecimal.ZERO)
                        .netWeightUnit(Unit.KILOGRAM)
                        .totalValue(BigDecimal.ZERO)
                        .totalUnit(Unit.EUR)
                        .build()
        ), vatRate);
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }

    private OrderSearchCriteriaDo mapToDo(final OrderSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new OrderSearchCriteriaDo(
                criteria.userIds(),
                timeUtil.toLocalDateTime(criteria.createdFrom()),
                timeUtil.toLocalDateTime(criteria.createdTo()),
                criteria.statuses(),
                priceUtil.countNoVatValue(criteria.totalFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.totalTo(), vatRate),
                criteria.totalUnit()
        );
    }

    private OrderData toOrderData(final OrderDo orderDo, final BigDecimal vatRate) {
        return OrderData.builder()
                .id(orderDo.getId())
                .userId(orderDo.getUserId())
                .created(timeUtil.toZonedDateTime(orderDo.getCreated()))
                .status(orderDo.getStatus())
                .orderNumber(orderDo.getOrderNumber())
                .description(orderDo.getDescription())
                .weightValue(orderDo.getWeightValue())
                .weightUnit(orderDo.getWeightUnit())
                .netWeightValue(orderDo.getNetWeightValue())
                .netWeightUnit(orderDo.getNetWeightUnit())
                .totalValue(orderDo.getTotalValue())
                .vatTotalValue(priceUtil.countVatValue(orderDo.getTotalValue(), vatRate))
                .totalUnit(orderDo.getTotalUnit())
                .build();
    }

    private OrderDo getOrderDo(final Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> WiwaException.ORDER_NOT_FOUND.exception("Order with id {0} not found", id));
    }
}
