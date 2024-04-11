package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WiwaOrderDto(
        Long id,
        String creator,
        LocalDateTime created,
        String modifier,
        LocalDateTime modified,
        String name,
        String status,
        BigDecimal totalValue,
        String totalUnit
) {

    public static Object[] toArray(final WiwaOrderDto wiwaOrderDto) {
        return new Object[]{
                wiwaOrderDto.id,
                wiwaOrderDto.creator,
                wiwaOrderDto.created,
                wiwaOrderDto.modifier,
                wiwaOrderDto.modified,
                wiwaOrderDto.name,
                wiwaOrderDto.status,
                wiwaOrderDto.totalValue,
                wiwaOrderDto.totalUnit
        };
    }

    public static WiwaOrderDto toObject(final Object[] array) {
        return new WiwaOrderDto(
                (Long) array[0],
                (String) array[1],
                (LocalDateTime) array[2],
                (String) array[3],
                (LocalDateTime) array[4],
                (String) array[5],
                (String) array[6],
                (BigDecimal) array[7],
                (String) array[8]
        );
    }
}
