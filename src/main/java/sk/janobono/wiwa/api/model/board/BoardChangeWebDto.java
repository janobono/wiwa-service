package sk.janobono.wiwa.api.model.board;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record BoardChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotBlank @Size(max = 255) String boardCode,
        @NotBlank @Size(max = 255) String structureCode,
        @NotNull Boolean orientation,
        @NotNull @Min(0) BigDecimal saleValue,
        @NotNull Unit saleUnit,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        @NotNull @Min(0) BigDecimal lengthValue,
        @NotNull Unit lengthUnit,
        @NotNull @Min(0) BigDecimal widthValue,
        @NotNull Unit widthUnit,
        @NotNull @Min(0) BigDecimal thicknessValue,
        @NotNull Unit thicknessUnit,
        @NotNull @Min(0) BigDecimal priceValue,
        @NotNull Unit priceUnit
) {
}
