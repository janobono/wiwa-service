package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record WiwaOrderDto(
        Long id,
        Long userId,
        LocalDateTime created,
        String status,
        Long orderNumber,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal totalValue,
        String totalUnit,
        LocalDate delivery,
        LocalDateTime ready,
        LocalDateTime finished
) {

    public static Object[] toArray(final WiwaOrderDto wiwaOrderDto) {
        return new Object[]{
                wiwaOrderDto.id,
                wiwaOrderDto.userId,
                wiwaOrderDto.created,
                wiwaOrderDto.status,
                wiwaOrderDto.orderNumber,
                wiwaOrderDto.netWeightValue,
                wiwaOrderDto.netWeightUnit,
                wiwaOrderDto.totalValue,
                wiwaOrderDto.totalUnit,
                wiwaOrderDto.delivery,
                wiwaOrderDto.ready,
                wiwaOrderDto.finished
        };
    }

    public static WiwaOrderDto toObject(final Object[] array) {
        return new WiwaOrderDto(
                (Long) array[0],
                (Long) array[1],
                (LocalDateTime) array[2],
                (String) array[3],
                (Long) array[4],
                (BigDecimal) array[5],
                (String) array[6],
                (BigDecimal) array[7],
                (String) array[8],
                (LocalDate) array[9],
                (LocalDateTime) array[10],
                (LocalDateTime) array[11]
        );
    }
}
