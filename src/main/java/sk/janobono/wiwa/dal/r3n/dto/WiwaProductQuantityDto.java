package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaProductQuantityDto(
        Long id,
        Long productId,
        String key,
        BigDecimal value,
        String unit
) {

    public static Object[] toArray(final WiwaProductQuantityDto wiwaProductQuantityDto) {
        return new Object[]{
                wiwaProductQuantityDto.id,
                wiwaProductQuantityDto.productId,
                wiwaProductQuantityDto.key,
                wiwaProductQuantityDto.value,
                wiwaProductQuantityDto.unit
        };
    }

    public static WiwaProductQuantityDto toObject(final Object[] array) {
        return new WiwaProductQuantityDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (BigDecimal) array[3],
                (String) array[4]
        );
    }
}
