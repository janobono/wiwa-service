package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;

import java.util.Optional;

public interface OrderViewRepository {

    Optional<OrderViewDo> findById(long id);

    Page<OrderViewDo> findAll(OrderViewSearchCriteriaDo criteria, Pageable pageable);
}
