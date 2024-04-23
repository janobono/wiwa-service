package sk.janobono.wiwa.api.model.board;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.api.model.QuantityWebDto;

import java.math.BigDecimal;

public record BoardChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotBlank @Size(max = 255) String boardCode,
        @NotBlank @Size(max = 255) String structureCode,
        @NotNull Boolean orientation,
        @NotNull QuantityWebDto sale,
        QuantityWebDto netWeight,
        @NotNull QuantityWebDto length,
        @NotNull QuantityWebDto width,
        @NotNull QuantityWebDto thickness,
        @NotNull @Min(0) BigDecimal price
) {
}
