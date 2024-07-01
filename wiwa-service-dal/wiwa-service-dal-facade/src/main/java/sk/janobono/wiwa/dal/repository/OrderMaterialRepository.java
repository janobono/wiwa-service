package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderMaterialDo;

import java.util.List;
import java.util.Optional;

public interface OrderMaterialRepository {

    int countById(long orderId, long materialId, String code);

    void deleteById(long orderId, long materialId, String code);

    Optional<OrderMaterialDo> findById(long orderId, long materialId, String code);

    List<OrderMaterialDo> findAllByOrderId(long orderId);

    OrderMaterialDo save(OrderMaterialDo orderMaterialDo);
}
