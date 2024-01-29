package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceDataSo(
        @NotNull LocalDate validFrom,
        @NotNull BigDecimal value,
        @NotNull Unit unit
) {
}
