package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.BoardPosition;

public record OrderItemPartImageWebDto(
        @NotNull BoardPosition boardPosition,
        @NotEmpty String image
) {
}
