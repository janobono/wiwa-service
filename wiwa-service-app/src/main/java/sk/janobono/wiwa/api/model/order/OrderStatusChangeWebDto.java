package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.OrderStatus;

public record OrderStatusChangeWebDto(
        @NotNull Boolean notifyUser,
        String comment,
        @NotNull OrderStatus newStatus
) {
}
