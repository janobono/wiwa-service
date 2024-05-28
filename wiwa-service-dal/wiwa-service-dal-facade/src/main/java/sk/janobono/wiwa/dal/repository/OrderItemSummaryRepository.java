package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;

import java.util.List;

public interface OrderItemSummaryRepository {

    void deleteByOrderItemId(long orderItemId);

    List<OrderItemSummaryDo> findAllByOrderItemId(long orderItemId);

    OrderItemSummaryDo insert(OrderItemSummaryDo orderItemSummary);
}
