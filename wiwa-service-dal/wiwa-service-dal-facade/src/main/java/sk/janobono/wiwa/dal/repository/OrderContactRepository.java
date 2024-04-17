package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.model.BaseOrderContactDo;

import java.util.Optional;

public interface OrderContactRepository {

    Optional<OrderContactDo> findByOrderId(final Long orderId);

    Page<BaseOrderContactDo> findByUserId(final Long userId, final Pageable pageable);

    OrderContactDo save(final OrderContactDo orderContactDo);
}
