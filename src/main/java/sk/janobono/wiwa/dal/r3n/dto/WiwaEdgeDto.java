package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaEdgeDto(
        Long id,
        String code,
        String name,
        String description,
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

    public static Object[] toArray(final WiwaEdgeDto wiwaEdgeDto) {
        return new Object[]{
                wiwaEdgeDto.id,
                wiwaEdgeDto.code,
                wiwaEdgeDto.name,
                wiwaEdgeDto.description,
                wiwaEdgeDto.saleValue,
                wiwaEdgeDto.saleUnit,
                wiwaEdgeDto.weightValue,
                wiwaEdgeDto.weightUnit,
                wiwaEdgeDto.netWeightValue,
                wiwaEdgeDto.netWeightUnit,
                wiwaEdgeDto.widthValue,
                wiwaEdgeDto.widthUnit,
                wiwaEdgeDto.thicknessValue,
                wiwaEdgeDto.thicknessUnit,
                wiwaEdgeDto.priceValue,
                wiwaEdgeDto.priceUnit
        };
    }

    public static WiwaEdgeDto toObject(final Object[] array) {
        return new WiwaEdgeDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (BigDecimal) array[4],
                (String) array[5],
                (BigDecimal) array[6],
                (String) array[7],
                (BigDecimal) array[8],
                (String) array[9],
                (BigDecimal) array[10],
                (String) array[11],
                (BigDecimal) array[12],
                (String) array[13],
                (BigDecimal) array[14],
                (String) array[15]
        );
    }
}
