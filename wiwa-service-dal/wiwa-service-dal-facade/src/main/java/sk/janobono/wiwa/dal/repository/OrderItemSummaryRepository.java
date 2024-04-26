package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;

import java.util.List;

public interface OrderItemSummaryRepository {

    List<OrderItemSummaryDo> findAllByOrderItemId(long orderItemId);

    void saveAll(long orderItemId, List<OrderItemSummaryDo> batch);
}
