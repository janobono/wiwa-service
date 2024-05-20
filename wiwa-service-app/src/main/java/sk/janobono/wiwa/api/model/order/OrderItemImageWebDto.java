package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ItemImage;

public record OrderItemImageWebDto(
        @NotNull ItemImage itemImage,
        @NotEmpty String image
) {
}
