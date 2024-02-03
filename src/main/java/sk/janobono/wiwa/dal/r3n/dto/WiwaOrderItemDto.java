package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        String creator,
        LocalDateTime created,
        String modifier,
        LocalDateTime modified,
        String type,
        String code,
        String name,
        BigDecimal priceValue,
        String priceUnit,
        BigDecimal amount,
        BigDecimal total
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.creator,
                wiwaOrderItemDto.created,
                wiwaOrderItemDto.modifier,
                wiwaOrderItemDto.modified,
                wiwaOrderItemDto.type,
                wiwaOrderItemDto.code,
                wiwaOrderItemDto.name,
                wiwaOrderItemDto.priceValue,
                wiwaOrderItemDto.priceUnit,
                wiwaOrderItemDto.amount,
                wiwaOrderItemDto.total
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (LocalDateTime) array[3],
                (String) array[4],
                (LocalDateTime) array[5],
                (String) array[6],
                (String) array[7],
                (String) array[8],
                (BigDecimal) array[9],
                (String) array[10],
                (BigDecimal) array[11],
                (BigDecimal) array[12]
        );
    }
}
