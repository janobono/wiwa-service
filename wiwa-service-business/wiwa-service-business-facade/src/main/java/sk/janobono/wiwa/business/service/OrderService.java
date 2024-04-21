package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.order.*;

import java.util.List;

public interface OrderService {

    Page<OrderData> getOrders(OrderSearchCriteriaData criteria, Pageable pageable);

    Page<OrderContactData> getOrderContacts(Long userId, Pageable pageable);

    OrderData getOrder(Long id);

    OrderData addOrder(Long userId);

    void deleteOrder(Long id);

    Long getOrderCreatorId(Long id);

    OrderSummaryData getOrderSummary(Long id);

    OrderData sendOrder(Long id, SendOrderData sendOrder);

    OrderData setOrderStatus(Long id, OrderStatusChangeData orderStatusChange);

    List<OrderCommentData> getComments(Long id);

    List<OrderCommentData> addComment(Long id, Long creatorId, OrderCommentChangeData orderCommentChange);

    List<OrderItemData> getOrderItems(Long id);

    OrderItemData addItem(Long id, Long creatorId, OrderItemChangeData orderItemChange);

    OrderItemData setItem(Long id, Long itemId, Long modifierId, OrderItemChangeData orderItemChange);

    OrderItemData moveUpItem(Long id, Long itemId, Long modifierId);

    OrderItemData moveDownItem(Long id, Long itemId, Long modifierId);

    void deleteItem(Long id, Long itemId);
}
