package sk.janobono.wiwa.api.model.order.item.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartCornerChangeRoundedWebDto(
        @NotNull @Min(0) BigDecimal radius
) implements PartCornerChangeWebDto {
}
