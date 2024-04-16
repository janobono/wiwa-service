package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        String name,
        Integer sortNum,
        String description,
        BigDecimal weightValue,
        String weightUnit,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal priceValue,
        String priceUnit,
        BigDecimal amountValue,
        String amountUnit,
        BigDecimal totalValue,
        String totalUnit
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.name,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.description,
                wiwaOrderItemDto.weightValue,
                wiwaOrderItemDto.weightUnit,
                wiwaOrderItemDto.netWeightValue,
                wiwaOrderItemDto.netWeightUnit,
                wiwaOrderItemDto.priceValue,
                wiwaOrderItemDto.priceUnit,
                wiwaOrderItemDto.amountValue,
                wiwaOrderItemDto.amountUnit,
                wiwaOrderItemDto.totalValue,
                wiwaOrderItemDto.totalUnit
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (Integer) array[3],
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
                (String) array[14]
        );
    }
}
