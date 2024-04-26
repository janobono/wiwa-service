package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ManufacturePropertiesWebDto(
        @NotNull @Min(0) BigDecimal minimalBoardDimension,
        @NotNull @Min(0) BigDecimal minimalEdgedBoardDimension,
        @NotNull @Min(0) BigDecimal minimalLayeredBoardDimension,
        @NotNull @Min(0) BigDecimal minimalFrameBoardDimension,
        @NotNull @Min(0) BigDecimal minimalCornerStraightDimension,
        @NotNull @Min(0) BigDecimal minimalCornerRoundedDimension,
        @NotNull @Min(0) BigDecimal edgeWidthAppendDimension,
        @NotNull @Min(0) BigDecimal edgeLengthAppendDimension,
        @NotNull @Min(0) BigDecimal layeredBoardAppendDimension
) {
}
