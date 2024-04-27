package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DimensionsWebDto(@NotNull @Min(0) BigDecimal x, @NotNull @Min(0) BigDecimal y) {
}
