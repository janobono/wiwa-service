package sk.janobono.wiwa.business.impl.util;

import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderItemChangeData;
import sk.janobono.wiwa.business.model.order.OrderItemData;
import sk.janobono.wiwa.business.model.order.OrderStatusChangeData;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.model.OrderAttributeKey;

@Service
public class OrderUtilService {

    public OrderData recountOrder(OrderDo order, UserDo modifier) {
        return null;
    }

    public void deleteItem(OrderDo order, UserDo modifier, long itemId) {
//
//        orderItemUtil.deleteItem(itemId);
//
//        saveOrder(order, orderItemUtil);
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));
    }

    public OrderItemData moveDownItem(OrderDo order, UserDo modifier, long itemId) {
//        final OrderItemData orderItemData = orderItemUtil.moveDownItem(itemId, toOrderUser(modifier));
//
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        return null;
    }

    public OrderItemData moveUpItem(OrderDo order, UserDo modifier, long itemId) {
//
//        final OrderItemData orderItemData = orderItemUtil.moveUpItem(itemId, toOrderUser(modifier));
//
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
        return null;
    }

    public OrderItemData setItem(OrderDo order, UserDo modifier, long itemId, OrderItemChangeData orderItemChange) {
//        final OrderItemData orderItemData = orderItemUtil.setItem(itemId, toOrderUser(modifier), orderItemChange);
//
//        saveOrder(order, orderItemUtil);
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));
        return null;
    }

    public OrderItemData addItem(OrderDo order, UserDo creator, OrderItemChangeData orderItemChange) {
//
//        final OrderItemData orderItemData = orderItemUtil.addItem(toOrderUser(creator), orderItemChange);
//
//        saveOrder(order, orderItemUtil);
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.ITEMS, orderItemUtil.getOrderItems()));
//        orderAttributeRepository.save(orderAttributeUtil.serializeValue(id, OrderAttributeKey.SUMMARY, orderItemUtil.getOrderSummary()));
//
//        return orderItemData;
        return null;
    }

    public OrderData setOrderStatus(OrderDo order, UserDo modifier, OrderStatusChangeData orderStatusChange) {
        return null;
    }
}
