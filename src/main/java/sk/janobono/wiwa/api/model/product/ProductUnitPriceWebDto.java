package sk.janobono.wiwa.api.model.product;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceWebDto(
        LocalDate validFrom,
        BigDecimal value,
        BigDecimal vatValue,
        Unit unit
) {
}
