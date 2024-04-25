package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.model.OderItemSortNumDo;
import sk.janobono.wiwa.dal.model.OderItemSummaryDo;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    int countByOrderId(long orderId);

    void deleteById(long id);

    Optional<OrderItemDo> findById(long id);

    Optional<OrderItemDo> findByOrderIdAndSortNum(long orderId, int sortNum);

    List<OrderItemDo> findAllByOrderId(long orderId);

    OrderItemDo insert(OrderItemDo orderItemDo);

    void setSortNums(List<OderItemSortNumDo> sortNums);

    void setData(long id, String data);

    void setSummary(long id, OderItemSummaryDo oderItemSummary);
}
