package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderMaterialDo;

import java.util.List;
import java.util.Optional;

public interface OrderMaterialRepository {

    int countByOrderIdAndMaterialIdAndCode(long orderId, long materialId, String code);

    void deleteByOrderIdAndMaterialIdAndCode(long orderId, long materialId, String code);

    Optional<OrderMaterialDo> findByOrderIdAndMaterialIdAndCode(long orderId, long materialId, String code);

    List<OrderMaterialDo> findAllByOrderId(long orderId);

    OrderMaterialDo save(OrderMaterialDo orderMaterialDo);
}
