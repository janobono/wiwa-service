package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.Quantity;

public record ManufacturePropertiesWebDto(
        @NotNull Quantity minimalBoardDimension,
        @NotNull Quantity minimalEdgedBoardDimension,
        @NotNull Quantity minimalLayeredBoardDimension,
        @NotNull Quantity minimalFrameBoardDimension,
        @NotNull Quantity edgeWidthAppendDimension,
        @NotNull Quantity edgeLengthAppendDimension,
        @NotNull Quantity layeredBoardAppendDimension
) {
}
