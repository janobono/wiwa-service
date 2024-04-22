package sk.janobono.wiwa.api.model.edge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

public record EdgeChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull Quantity sale,
        Quantity netWeight,
        @NotNull Quantity width,
        @NotNull Quantity thickness,
        @NotNull Money price
) {
}
