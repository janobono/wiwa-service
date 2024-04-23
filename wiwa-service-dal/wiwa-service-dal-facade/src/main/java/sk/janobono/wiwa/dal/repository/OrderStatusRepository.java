package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderStatusDo;

import java.util.List;

public interface OrderStatusRepository {

    List<OrderStatusDo> findAllByOrderId(long orderId);

    OrderStatusDo save(OrderStatusDo orderStatusDo);
}
