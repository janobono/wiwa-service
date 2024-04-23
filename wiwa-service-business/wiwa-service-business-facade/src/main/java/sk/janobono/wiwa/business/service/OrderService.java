package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.order.*;

import java.util.List;

public interface OrderService {

    Page<OrderData> getOrders(OrderSearchCriteriaData criteria, Pageable pageable);

    Page<OrderContactData> getOrderContacts(long userId, Pageable pageable);

    OrderContactData setOrderContact(long id, OrderContactData orderContact);

    OrderData getOrder(long id);

    OrderData addOrder(long userId);

    void deleteOrder(long id);

    Long getOrderCreatorId(long id);

    OrderSummaryData getOrderSummary(long id);

    OrderData recountOrder(long id, Long modifierId);

    byte[] getPdf(long id);

    byte[] getCsv(long id);

    OrderData sendOrder(long id, long modifierId, SendOrderData sendOrder);

    OrderData setOrderStatus(long id, long modifierId, OrderStatusChangeData orderStatusChange);

    List<OrderCommentData> getComments(long id);

    List<OrderCommentData> addComment(long id, long creatorId, OrderCommentChangeData orderCommentChange);

    List<OrderItemData> getOrderItems(long id);

    OrderItemData addItem(long id, long creatorId, OrderItemChangeData orderItemChange, boolean manager);

    OrderItemData setItem(long id, long itemId, long modifierId, OrderItemChangeData orderItemChange, boolean manager);

    OrderItemData moveUpItem(long id, long itemId, long modifierId, boolean manager);

    OrderItemData moveDownItem(long id, long itemId, long modifierId, boolean manager);

    void deleteItem(long id, long itemId, long modifierId, boolean manager);
}
