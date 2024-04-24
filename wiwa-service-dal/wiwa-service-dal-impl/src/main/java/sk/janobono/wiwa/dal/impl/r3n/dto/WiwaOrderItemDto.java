package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Integer sortNum,
        String name,
        BigDecimal partPrice,
        BigDecimal partNetWeight,
        BigDecimal amount,
        BigDecimal netWeight,
        BigDecimal total,
        String data
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.name,
                wiwaOrderItemDto.partPrice,
                wiwaOrderItemDto.partNetWeight,
                wiwaOrderItemDto.amount,
                wiwaOrderItemDto.netWeight,
                wiwaOrderItemDto.total,
                wiwaOrderItemDto.data
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Integer) array[2],
                (String) array[3],
                (BigDecimal) array[4],
                (BigDecimal) array[5],
                (BigDecimal) array[6],
                (BigDecimal) array[7],
                (BigDecimal) array[8],
                (String) array[9]
        );
    }
}