package sk.janobono.wiwa.api.model.board;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BoardChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotBlank @Size(max = 255) String boardCode,
        @NotBlank @Size(max = 255) String structureCode,
        @NotNull Boolean orientation,
        BigDecimal weight,
        @NotNull @Min(0) BigDecimal length,
        @NotNull @Min(0) BigDecimal width,
        @NotNull @Min(0) BigDecimal thickness,
        @NotNull @Min(0) BigDecimal price
) {
}
