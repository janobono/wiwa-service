package sk.janobono.wiwa.dal.r3n.dto;

import java.time.LocalDateTime;

public record WiwaTaskDto(
        Long id,
        String creator,
        LocalDateTime created,
        String type,
        String status,
        String data,
        String log
) {

    public static Object[] toArray(final WiwaTaskDto wiwaTaskDto) {
        return new Object[]{
                wiwaTaskDto.id,
                wiwaTaskDto.creator,
                wiwaTaskDto.created,
                wiwaTaskDto.type,
                wiwaTaskDto.status,
                wiwaTaskDto.data,
                wiwaTaskDto.log
        };
    }

    public static WiwaTaskDto toObject(final Object[] array) {
        return new WiwaTaskDto(
                (Long) array[0],
                (String) array[1],
                (LocalDateTime) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (String) array[6]
        );
    }
}
