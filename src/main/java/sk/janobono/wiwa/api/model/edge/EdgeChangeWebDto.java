package sk.janobono.wiwa.api.model.edge;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record EdgeChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull @Min(0) BigDecimal saleValue,
        @NotNull Unit saleUnit,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        @NotNull @Min(0) BigDecimal widthValue,
        @NotNull Unit widthUnit,
        @NotNull @Min(0) BigDecimal thicknessValue,
        @NotNull Unit thicknessUnit,
        @NotNull @Min(0) BigDecimal priceValue,
        @NotNull Unit priceUnit
) {
}
