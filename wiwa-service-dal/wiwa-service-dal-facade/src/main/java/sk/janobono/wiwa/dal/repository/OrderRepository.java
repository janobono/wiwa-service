package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderDeliveryDo;
import sk.janobono.wiwa.dal.model.OrderSummaryDo;

import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo insert(OrderDo orderDo);

    void setDelivery(long id, OrderDeliveryDo orderDelivery);

    void setData(long id, String data);

    void setOrderSummary(long id, OrderSummaryDo orderSummary);
}
