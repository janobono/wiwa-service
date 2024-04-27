package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.DimensionsWebDto;

public record PartCornerStraightWebDto(@NotNull DimensionsWebDto dimensions) implements PartCornerWebDto {
}
