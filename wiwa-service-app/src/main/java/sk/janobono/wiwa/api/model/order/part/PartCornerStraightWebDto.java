package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartCornerStraightWebDto(
        @NotNull @Min(0) BigDecimal dimensionX,
        @NotNull @Min(0) BigDecimal dimensionY
) implements PartCornerWebDto {
}
