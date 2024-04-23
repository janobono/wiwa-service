package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Integer sortNum,
        String data,
        BigDecimal netWeight,
        BigDecimal total
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.data,
                wiwaOrderItemDto.netWeight,
                wiwaOrderItemDto.total
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Integer) array[2],
                (String) array[3],
                (BigDecimal) array[4],
                (BigDecimal) array[5]
        );
    }
}
