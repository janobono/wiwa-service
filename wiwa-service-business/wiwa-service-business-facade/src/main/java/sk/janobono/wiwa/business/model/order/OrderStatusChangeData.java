package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.model.OrderStatus;

public record OrderStatusChangeData(
        Boolean notifyUser,
        String comment,
        OrderStatus newStatus
) {
}
