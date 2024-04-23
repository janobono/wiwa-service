package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record QuantityWebDto(@NotNull @Min(0) BigDecimal quantity, @NotNull Unit unit) {
}
