package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.QuantityWebDto;

public record ManufacturePropertiesWebDto(
        @NotNull QuantityWebDto minimalBoardDimension,
        @NotNull QuantityWebDto minimalEdgedBoardDimension,
        @NotNull QuantityWebDto minimalLayeredBoardDimension,
        @NotNull QuantityWebDto minimalFrameBoardDimension,
        @NotNull QuantityWebDto edgeWidthAppendDimension,
        @NotNull QuantityWebDto edgeLengthAppendDimension,
        @NotNull QuantityWebDto layeredBoardAppendDimension
) {
}
