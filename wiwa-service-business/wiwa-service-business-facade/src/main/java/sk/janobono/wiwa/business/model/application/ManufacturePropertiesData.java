package sk.janobono.wiwa.business.model.application;

import java.math.BigDecimal;

public record ManufacturePropertiesData(
        BigDecimal minimalBoardDimension,
        BigDecimal minimalEdgedBoardDimension,
        BigDecimal minimalLayeredBoardDimension,
        BigDecimal minimalFrameBoardDimension,
        BigDecimal minimalCornerStraightDimension,
        BigDecimal minimalCornerRoundedDimension,
        BigDecimal edgeWidthAppendDimension,
        BigDecimal edgeLengthAppendDimension,
        BigDecimal layeredBoardAppendDimension
) {
}
