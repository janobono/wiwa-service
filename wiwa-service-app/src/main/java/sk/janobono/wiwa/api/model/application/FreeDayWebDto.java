package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FreeDayWebDto(
        @NotBlank String name,
        @NotNull @Min(1) @Max(31) Integer day,
        @NotNull @Min(1) @Max(12) Integer month
) {
}
