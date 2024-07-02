package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.order.part.PartWebDto;

public record OrderItemChangeWebDto(
        @NotBlank String name,
        String description,
        @NotNull @Min(0) Integer quantity,
        @NotNull PartWebDto part
) {
}
