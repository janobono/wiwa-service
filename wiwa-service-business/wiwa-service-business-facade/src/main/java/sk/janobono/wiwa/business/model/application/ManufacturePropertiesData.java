package sk.janobono.wiwa.business.model.application;

import java.math.BigDecimal;

public record ManufacturePropertiesData(
        ManufactureDimensionsData minimalSystemDimensions,
        ManufactureDimensionsData minimalEdgedBoardDimensions,
        ManufactureDimensionsData minimalLayeredBoardDimensions,
        ManufactureDimensionsData minimalFrameBoardDimensions,
        BigDecimal edgeWidthAppend,
        BigDecimal edgeLengthAppend,
        BigDecimal duplicatedBoardAppend
) {
}
