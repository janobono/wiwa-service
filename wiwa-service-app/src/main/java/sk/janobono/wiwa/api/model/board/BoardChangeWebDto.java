package sk.janobono.wiwa.api.model.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record BoardChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotBlank @Size(max = 255) String boardCode,
        @NotBlank @Size(max = 255) String structureCode,
        @NotNull Boolean orientation,
        @NotNull Quantity sale,
        Quantity weight,
        Quantity netWeight,
        @NotNull Quantity length,
        @NotNull Quantity width,
        @NotNull Quantity thickness,
        @NotNull Money price
) {
}
