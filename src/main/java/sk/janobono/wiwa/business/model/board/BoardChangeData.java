package sk.janobono.wiwa.business.model.board;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record BoardChangeData(
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        BigDecimal saleValue,
        Unit saleUnit,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        BigDecimal lengthValue,
        Unit lengthUnit,
        BigDecimal widthValue,
        Unit widthUnit,
        BigDecimal thicknessValue,
        Unit thicknessUnit,
        BigDecimal priceValue,
        Unit priceUnit
) {
}
