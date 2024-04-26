package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;

import java.util.List;

public interface OrderSummaryViewRepository {

    List<OrderSummaryViewDo> findAllById(long id);
}
