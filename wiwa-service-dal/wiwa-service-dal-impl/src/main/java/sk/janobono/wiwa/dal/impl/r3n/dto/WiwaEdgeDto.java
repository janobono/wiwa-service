package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaEdgeDto(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal saleValue,
        String saleUnit,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal widthValue,
        String widthUnit,
        BigDecimal thicknessValue,
        String thicknessUnit,
        BigDecimal price
) {

    public static Object[] toArray(final WiwaEdgeDto wiwaEdgeDto) {
        return new Object[]{
                wiwaEdgeDto.id,
                wiwaEdgeDto.code,
                wiwaEdgeDto.name,
                wiwaEdgeDto.description,
                wiwaEdgeDto.saleValue,
                wiwaEdgeDto.saleUnit,
                wiwaEdgeDto.netWeightValue,
                wiwaEdgeDto.netWeightUnit,
                wiwaEdgeDto.widthValue,
                wiwaEdgeDto.widthUnit,
                wiwaEdgeDto.thicknessValue,
                wiwaEdgeDto.thicknessUnit,
                wiwaEdgeDto.price
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
                (BigDecimal) array[12]
        );
    }
}
