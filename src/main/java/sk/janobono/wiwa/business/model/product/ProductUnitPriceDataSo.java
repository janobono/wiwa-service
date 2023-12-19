package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductUnitPriceDataSo(
        @NotNull ZonedDateTime validFrom,
        @NotNull BigDecimal value,
        @NotEmpty String unitId
) {
}
