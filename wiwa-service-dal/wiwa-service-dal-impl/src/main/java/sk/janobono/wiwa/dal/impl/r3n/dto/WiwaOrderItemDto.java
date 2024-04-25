package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Integer sortNum,
        BigDecimal weight,
        BigDecimal total,
        String data
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.weight,
                wiwaOrderItemDto.total,
                wiwaOrderItemDto.data
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Integer) array[2],
                (BigDecimal) array[3],
                (BigDecimal) array[4],
                (String) array[5]
        );
    }
}
