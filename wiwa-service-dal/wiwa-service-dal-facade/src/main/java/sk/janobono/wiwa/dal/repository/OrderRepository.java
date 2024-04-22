package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;

import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    boolean existsById(long id);

    Page<OrderDo> findAll(OrderSearchCriteriaDo criteria, Pageable pageable);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo save(OrderDo orderDo);
}
