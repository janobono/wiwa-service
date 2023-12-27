package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaProductQuantityDto(
        Long id,
        Long productId,
        Long unitId,
        String key,
        BigDecimal value
) {

    public static Object[] toArray(final WiwaProductQuantityDto wiwaProductQuantityDto) {
        return new Object[]{
                wiwaProductQuantityDto.id,
                wiwaProductQuantityDto.productId,
                wiwaProductQuantityDto.unitId,
                wiwaProductQuantityDto.key,
                wiwaProductQuantityDto.value
        };
    }

    public static WiwaProductQuantityDto toObject(final Object[] array) {
        return new WiwaProductQuantityDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (String) array[3],
                (BigDecimal) array[4]
        );
    }
}
