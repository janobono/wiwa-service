package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.OrderItemChangeData;

public interface OrderService {

    Page<OrderData> getOrders(OrderSearchCriteriaData criteria, Pageable pageable);

    Page<OrderContactData> getOrderContacts(long userId, Pageable pageable);

    OrderData setOrderContact(long id, OrderContactData orderContact);

    OrderData getOrder(long id);

    OrderData addOrder(long userId);

    void deleteOrder(long id);

    Long getOrderCreatorId(long id);

    OrderData recountOrder(long id, Long modifierId);

    byte[] getPdf(long id);

    byte[] getCsv(long id);

    OrderData sendOrder(long id, long modifierId, SendOrderData sendOrder);

    OrderData setOrderStatus(long id, long modifierId, OrderStatusChangeData orderStatusChange);

    OrderData addComment(long id, long creatorId, OrderCommentChangeData orderCommentChange);

    OrderData addItem(long id, long creatorId, OrderItemChangeData orderItemChange, boolean manager);

    OrderData setItem(long id, long itemId, long modifierId, OrderItemChangeData orderItemChange, boolean manager);

    OrderData moveUpItem(long id, long itemId, long modifierId, boolean manager);

    OrderData moveDownItem(long id, long itemId, long modifierId, boolean manager);

    OrderData deleteItem(long id, long itemId, long modifierId, boolean manager);
}
