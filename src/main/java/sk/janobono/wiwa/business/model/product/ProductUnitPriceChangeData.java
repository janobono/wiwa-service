package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductUnitPriceChangeData(LocalDate validFrom, BigDecimal value, Unit unit) {
}
