package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.time.LocalDateTime;

public record WiwaOrderStatusDto(
        Long id,
        Long orderId,
        Long userId,
        LocalDateTime created,
        String status,
        String comment
) {

    public static Object[] toArray(final WiwaOrderStatusDto wiwaOrderStatusDto) {
        return new Object[]{
                wiwaOrderStatusDto.id,
                wiwaOrderStatusDto.orderId,
                wiwaOrderStatusDto.userId,
                wiwaOrderStatusDto.created,
                wiwaOrderStatusDto.status,
                wiwaOrderStatusDto.comment
        };
    }

    public static WiwaOrderStatusDto toObject(final Object[] array) {
        return new WiwaOrderStatusDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (LocalDateTime) array[3],
                (String) array[4],
                (String) array[5]
        );
    }
}
