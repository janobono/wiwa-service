package sk.janobono.wiwa.api.model.edge;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.api.model.QuantityWebDto;

import java.math.BigDecimal;

public record EdgeChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull QuantityWebDto sale,
        QuantityWebDto netWeight,
        @NotNull QuantityWebDto width,
        @NotNull QuantityWebDto thickness,
        @NotNull @Min(0) BigDecimal price
) {
}
