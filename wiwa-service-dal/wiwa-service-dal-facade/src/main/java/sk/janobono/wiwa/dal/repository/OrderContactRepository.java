package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.model.BaseOrderContactDo;

import java.util.Optional;

public interface OrderContactRepository {

    Optional<OrderContactDo> findByOrderId(Long orderId);

    Page<BaseOrderContactDo> findByUserId(Long userId, Pageable pageable);

    OrderContactDo save(OrderContactDo orderContactDo);
}
