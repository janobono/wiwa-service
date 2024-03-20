package sk.janobono.wiwa.dal.domain;

import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record EdgeProductViewDo(
        Long id,
        String code,
        String name,
        String description,
        ProductStockStatus stockStatus,
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
