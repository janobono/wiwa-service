package sk.janobono.wiwa.business.model.edge;

import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record EdgeChangeData(
        String code,
        String name,
        String description,
        BigDecimal saleValue,
        Unit saleUnit,
        BigDecimal weightValue,
        Unit weightUnit,
        BigDecimal netWeightValue,
        Unit netWeightUnit,
        BigDecimal widthValue,
        Unit widthUnit,
        BigDecimal thicknessValue,
        Unit thicknessUnit,
        BigDecimal priceValue,
        Unit priceUnit
) {
}
