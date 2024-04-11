package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.OrderStatus;

public record OrderSearchCriteriaDo(
        String creator,
        OrderStatus status,
        String searchField
) {
}
