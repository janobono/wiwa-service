package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.DimensionsWebDto;

import java.math.BigDecimal;

public record ManufacturePropertiesWebDto(
        @NotNull DimensionsWebDto minimalSystemDimensions,
        @NotNull DimensionsWebDto minimalEdgedBoardDimensions,
        @NotNull DimensionsWebDto minimalLayeredBoardDimensions,
        @NotNull DimensionsWebDto minimalFrameBoardDimensions,
        @NotNull @Min(0) BigDecimal edgeWidthAppend,
        @NotNull @Min(0) BigDecimal edgeLengthAppend,
        @NotNull @Min(0) BigDecimal duplicatedBoardAppend
) {
}
