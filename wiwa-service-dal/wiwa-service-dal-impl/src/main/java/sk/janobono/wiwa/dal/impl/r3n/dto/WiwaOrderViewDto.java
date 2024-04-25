package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WiwaOrderViewDto(
        Long id,
        Long userId,
        LocalDateTime created,
        Long orderNumber,
        LocalDate delivery,
        String packageType,
        String status,
        BigDecimal weight,
        BigDecimal total
) {

    public static Object[] toArray(final WiwaOrderViewDto wiwaOrderViewDto) {
        return new Object[]{
                wiwaOrderViewDto.id,
                wiwaOrderViewDto.userId,
                wiwaOrderViewDto.created,
                wiwaOrderViewDto.orderNumber,
                wiwaOrderViewDto.delivery,
                wiwaOrderViewDto.packageType,
                wiwaOrderViewDto.status,
                wiwaOrderViewDto.weight,
                wiwaOrderViewDto.total
        };
    }

    public static WiwaOrderViewDto toObject(final Object[] array) {
        return new WiwaOrderViewDto(
                (Long) array[0],
                (Long) array[1],
                (LocalDateTime) array[2],
                (Long) array[3],
                (LocalDate) array[4],
                (String) array[5],
                (String) array[6],
                (BigDecimal) array[7],
                (BigDecimal) array[8]
        );
    }
}
