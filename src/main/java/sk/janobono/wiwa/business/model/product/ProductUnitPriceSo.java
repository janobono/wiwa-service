package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceSo(
        LocalDate validFrom,
        BigDecimal value,
        BigDecimal vatValue,
        Unit unit
) {
}
