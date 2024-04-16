package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;

import java.util.Optional;

public interface OrderRepository {

    void deleteById(final Long id);

    boolean existsById(final Long id);

    Page<OrderDo> findAll(final OrderSearchCriteriaDo criteria, final Pageable pageable);

    Optional<OrderDo> findById(final Long id);

    OrderDo save(final OrderDo orderDo);
}
