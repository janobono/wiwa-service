package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WiwaOrderDto(
        Long id,
        Long userId,
        LocalDateTime created,
        Long orderNumber,
        String contact,
        LocalDate delivery,
        String packageType,
        BigDecimal weight,
        BigDecimal total,
        String summary
) {

    public static Object[] toArray(final WiwaOrderDto wiwaOrderDto) {
        return new Object[]{
                wiwaOrderDto.id,
                wiwaOrderDto.userId,
                wiwaOrderDto.created,
                wiwaOrderDto.orderNumber,
                wiwaOrderDto.contact,
                wiwaOrderDto.delivery,
                wiwaOrderDto.packageType,
                wiwaOrderDto.weight,
                wiwaOrderDto.total,
                wiwaOrderDto.summary
        };
    }

    public static WiwaOrderDto toObject(final Object[] array) {
        return new WiwaOrderDto(
                (Long) array[0],
                (Long) array[1],
                (LocalDateTime) array[2],
                (Long) array[3],
                (String) array[4],
                (LocalDate) array[5],
                (String) array[6],
                (BigDecimal) array[7],
                (BigDecimal) array[8],
                (String) array[9]
        );
    }
}
