package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ManufacturePropertiesWebDto(
        @NotNull ManufactureDimensionsWebDto minimalSystemDimensions,
        @NotNull ManufactureDimensionsWebDto minimalEdgedBoardDimensions,
        @NotNull ManufactureDimensionsWebDto minimalLayeredBoardDimensions,
        @NotNull ManufactureDimensionsWebDto minimalFrameBoardDimensions,
        @NotNull @Min(0) BigDecimal edgeWidthAppend,
        @NotNull @Min(0) BigDecimal edgeLengthAppend,
        @NotNull @Min(0) BigDecimal duplicatedBoardAppend
) {
}
