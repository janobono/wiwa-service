package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderDo;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo insert(OrderDo orderDo);

    void setDelivery(long id, LocalDate delivery);

    void setData(long id, String data);
}
