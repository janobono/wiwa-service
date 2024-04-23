package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.time.LocalDateTime;

public record WiwaOrderCommentDto(
        Long id,
        Long orderId,
        Long userId,
        LocalDateTime created,
        String comment
) {

    public static Object[] toArray(final WiwaOrderCommentDto wiwaOrderCommentDto) {
        return new Object[]{
                wiwaOrderCommentDto.id,
                wiwaOrderCommentDto.orderId,
                wiwaOrderCommentDto.userId,
                wiwaOrderCommentDto.created,
                wiwaOrderCommentDto.comment
        };
    }

    public static WiwaOrderCommentDto toObject(final Object[] array) {
        return new WiwaOrderCommentDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (LocalDateTime) array[3],
                (String) array[4]
        );
    }
}
