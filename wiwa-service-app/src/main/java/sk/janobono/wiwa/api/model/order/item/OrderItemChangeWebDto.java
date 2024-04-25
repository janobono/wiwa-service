package sk.janobono.wiwa.api.model.order.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.order.item.part.PartChangeWebDto;

public record OrderItemChangeWebDto(
        @NotBlank String name,
        @NotNull @Min(0) Integer amount,
        @NotNull PartChangeWebDto partChange
) {
}
