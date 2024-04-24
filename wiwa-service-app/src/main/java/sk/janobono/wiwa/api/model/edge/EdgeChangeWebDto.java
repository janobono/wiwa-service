package sk.janobono.wiwa.api.model.edge;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EdgeChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        BigDecimal weight,
        @NotNull @Min(0) BigDecimal width,
        @NotNull @Min(0) BigDecimal thickness,
        @NotNull @Min(0) BigDecimal price
) {
}
