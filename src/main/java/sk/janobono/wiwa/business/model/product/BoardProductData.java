package sk.janobono.wiwa.business.model.product;

import lombok.Builder;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BoardProductData(
        Long id,
        String code,
        String name,
        String description,
        ProductStockStatus stockStatus,
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
        BigDecimal vatPriceValue,
        Unit priceUnit,
        List<ApplicationImageInfoData> images
) {
}
