package sk.janobono.wiwa.business.model.ui;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Unit;

public record UnitSo(
        @NotNull Unit id,
        @NotEmpty String value
) {
}
