package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Unit;

public record UnitWebDto(@NotNull Unit id, @NotEmpty String value) {
}
