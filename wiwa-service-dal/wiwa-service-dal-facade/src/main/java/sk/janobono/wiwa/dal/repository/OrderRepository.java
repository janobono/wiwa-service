package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.model.OrderPackageType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository {

    void deleteById(long id);

    Optional<Long> getOrderUserId(long id);

    Optional<OrderDo> findById(long id);

    OrderDo insert(OrderDo orderDo);

    void setContact(long id, String contact);

    void setDelivery(long id, LocalDate delivery);

    void setPackageType(long id, OrderPackageType packageType);

    void setWeight(long id, BigDecimal weight);

    void setTotal(long id, BigDecimal total);

    void setSummary(long id, String summary);
}
