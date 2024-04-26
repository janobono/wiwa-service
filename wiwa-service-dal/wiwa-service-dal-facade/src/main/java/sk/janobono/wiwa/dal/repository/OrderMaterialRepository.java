package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.model.OrderMaterialIdDo;

import java.util.List;
import java.util.Optional;

public interface OrderMaterialRepository {

    int countById(OrderMaterialIdDo id);

    void deleteById(OrderMaterialIdDo id);

    Optional<OrderMaterialDo> findById(OrderMaterialIdDo id);

    List<OrderMaterialDo> findAllByOrderId(long orderId);

    OrderMaterialDo save(OrderMaterialDo orderMaterialDo);
}
