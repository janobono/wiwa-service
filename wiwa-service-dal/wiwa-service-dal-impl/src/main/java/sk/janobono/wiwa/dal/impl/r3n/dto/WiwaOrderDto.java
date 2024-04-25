package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WiwaOrderDto(
        Long id,
        Long userId,
        LocalDateTime created,
        Long orderNumber,
        LocalDate delivery,
        String packageType,
        String data
) {

    public static Object[] toArray(final WiwaOrderDto wiwaOrderDto) {
        return new Object[]{
                wiwaOrderDto.id,
                wiwaOrderDto.userId,
                wiwaOrderDto.created,
                wiwaOrderDto.orderNumber,
                wiwaOrderDto.delivery,
                wiwaOrderDto.packageType,
                wiwaOrderDto.data
        };
    }

    public static WiwaOrderDto toObject(final Object[] array) {
        return new WiwaOrderDto(
                (Long) array[0],
                (Long) array[1],
                (LocalDateTime) array[2],
                (Long) array[3],
                (LocalDate) array[4],
                (String) array[5],
                (String) array[6]
        );
    }
}
