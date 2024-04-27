package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.business.model.DimensionsData;

import java.math.BigDecimal;

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
