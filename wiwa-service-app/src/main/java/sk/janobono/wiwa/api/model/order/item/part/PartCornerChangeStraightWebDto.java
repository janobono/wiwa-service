package sk.janobono.wiwa.api.model.order.item.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartCornerChangeStraightWebDto(
        @NotNull @Min(0) BigDecimal dimensionX,
        @NotNull @Min(0) BigDecimal dimensionY
) implements PartCornerChangeWebDto {
}
