package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceChangeWebDto(
        @NotNull LocalDate validFrom,
        @NotNull BigDecimal value,
        @NotNull Unit unit
) {
}
