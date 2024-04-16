package sk.janobono.wiwa.api.model.board;

import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public record BoardWebDto(
        Long id,
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
        BigDecimal vatPriceValue,
        Unit priceUnit,
        List<ApplicationImageInfoWebDto> images,
        List<BoardCategoryItemWebDto> categoryItems
) {
}
