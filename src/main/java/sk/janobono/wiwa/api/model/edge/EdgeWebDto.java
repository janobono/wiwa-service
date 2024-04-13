package sk.janobono.wiwa.api.model.edge;

import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public record EdgeWebDto(
        Long id,
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
        BigDecimal vatPriceValue,
        Unit priceUnit,
        List<ApplicationImageInfoWebDto> images,
        List<EdgeCategoryItemWebDto> categoryItems
) {
}
