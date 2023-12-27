package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceSo(
        @NotNull LocalDate validFrom,
        @NotNull BigDecimal value,
        @NotNull Long unitId
) {
}
