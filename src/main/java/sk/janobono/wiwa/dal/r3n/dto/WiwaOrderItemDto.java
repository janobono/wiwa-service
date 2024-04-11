package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Long parentId,
        String creator,
        LocalDateTime created,
        String modifier,
        LocalDateTime modified,
        String type,
        String code,
        String name,
        BigDecimal priceValue,
        String priceUnit,
        BigDecimal amountValue,
        String amountUnit,
        BigDecimal totalValue,
        String totalUnit,
        String data
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.parentId,
                wiwaOrderItemDto.creator,
                wiwaOrderItemDto.created,
                wiwaOrderItemDto.modifier,
                wiwaOrderItemDto.modified,
                wiwaOrderItemDto.type,
                wiwaOrderItemDto.code,
                wiwaOrderItemDto.name,
                wiwaOrderItemDto.priceValue,
                wiwaOrderItemDto.priceUnit,
                wiwaOrderItemDto.amountValue,
                wiwaOrderItemDto.amountUnit,
                wiwaOrderItemDto.totalValue,
                wiwaOrderItemDto.totalUnit,
                wiwaOrderItemDto.data
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (String) array[3],
                (LocalDateTime) array[4],
                (String) array[5],
                (LocalDateTime) array[6],
                (String) array[7],
                (String) array[8],
                (String) array[9],
                (BigDecimal) array[10],
                (String) array[11],
                (BigDecimal) array[12],
                (String) array[13],
                (BigDecimal) array[14],
                (String) array[15],
                (String) array[16]
        );
    }
}
