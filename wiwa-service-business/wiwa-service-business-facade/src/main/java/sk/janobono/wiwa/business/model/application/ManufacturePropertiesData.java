package sk.janobono.wiwa.business.model.application;

import lombok.Builder;
import sk.janobono.wiwa.business.model.DimensionsData;

import java.math.BigDecimal;

@Builder
public record ManufacturePropertiesData(
        DimensionsData minimalSystemDimensions,
        DimensionsData minimalEdgedBoardDimensions,
        DimensionsData minimalLayeredBoardDimensions,
        DimensionsData minimalFrameBoardDimensions,
        BigDecimal edgeWidthAppend,
        BigDecimal edgeLengthAppend,
        BigDecimal duplicatedBoardAppend
) {
}
