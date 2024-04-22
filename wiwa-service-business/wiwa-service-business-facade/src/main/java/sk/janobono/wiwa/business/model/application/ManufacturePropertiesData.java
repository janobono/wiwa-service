package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.Quantity;

public record ManufacturePropertiesData(
        Quantity minimalBoardDimension,
        Quantity minimalEdgedBoardDimension,
        Quantity minimalLayeredBoardDimension,
        Quantity minimalFrameBoardDimension,
        Quantity edgeWidthAppendDimension,
        Quantity edgeLengthAppendDimension,
        Quantity layeredBoardAppendDimension
) {
}
