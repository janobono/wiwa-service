package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartCornerRoundedWebDto(
        Long edgeId,
        @NotNull @Min(0) BigDecimal radius) implements PartCornerWebDto {
}
