package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.order.OrderChangeData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderSearchCriteriaData;

public interface OrderService {

    Page<OrderData> getOrders(final OrderSearchCriteriaData criteria, final Pageable pageable);

    OrderData getOrder(final Long id);

    OrderData addOrder(final Long userId, final OrderChangeData orderChangeData);

    void deleteOrder(final Long id);
}
