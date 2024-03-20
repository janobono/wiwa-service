package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaEdgeProductViewDto(
        Long id,
        String code,
        String name,
        String description,
        String stockStatus,
        BigDecimal saleValue,
        String saleUnit,
        BigDecimal weightValue,
        String weightUnit,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal widthValue,
        String widthUnit,
        BigDecimal thicknessValue,
        String thicknessUnit,
        BigDecimal priceValue,
        String priceUnit
) {

    public static Object[] toArray(final WiwaEdgeProductViewDto wiwaEdgeProductViewDto) {
        return new Object[]{
                wiwaEdgeProductViewDto.id,
                wiwaEdgeProductViewDto.code,
                wiwaEdgeProductViewDto.name,
                wiwaEdgeProductViewDto.description,
                wiwaEdgeProductViewDto.stockStatus,
                wiwaEdgeProductViewDto.saleValue,
                wiwaEdgeProductViewDto.saleUnit,
                wiwaEdgeProductViewDto.weightValue,
                wiwaEdgeProductViewDto.weightUnit,
                wiwaEdgeProductViewDto.netWeightValue,
                wiwaEdgeProductViewDto.netWeightUnit,
                wiwaEdgeProductViewDto.widthValue,
                wiwaEdgeProductViewDto.widthUnit,
                wiwaEdgeProductViewDto.thicknessValue,
                wiwaEdgeProductViewDto.thicknessUnit,
                wiwaEdgeProductViewDto.priceValue,
                wiwaEdgeProductViewDto.priceUnit
        };
    }

    public static WiwaEdgeProductViewDto toObject(final Object[] array) {
        return new WiwaEdgeProductViewDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (BigDecimal) array[5],
                (String) array[6],
                (BigDecimal) array[7],
                (String) array[8],
                (BigDecimal) array[9],
                (String) array[10],
                (BigDecimal) array[11],
                (String) array[12],
                (BigDecimal) array[13],
                (String) array[14],
                (BigDecimal) array[15],
                (String) array[16]
        );
    }
}
