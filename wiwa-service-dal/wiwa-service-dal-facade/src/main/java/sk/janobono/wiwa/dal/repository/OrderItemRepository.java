package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderItemDo;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    int countByOrderId(long orderId);

    void deleteById(long id);

    Optional<OrderItemDo> findById(long id);

    Optional<OrderItemDo> findByOrderIdAndSortNum(long orderId, int sortNum);

    List<OrderItemDo> findAllByOrderId(long orderId);

    OrderItemDo insert(OrderItemDo orderItemDo);

    void setSortNum(long id, int sortNum);

    void setName(long id, String name);

    void setDescription(long id, String description);

    void setQuantity(long id, int quantity);

    void setPart(long id, String part);
}
