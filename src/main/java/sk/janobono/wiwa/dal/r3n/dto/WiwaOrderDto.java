package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WiwaOrderDto(
        Long id,
        String creator,
        LocalDateTime created,
        String status,
        Long orderNumber,
        String description,
        BigDecimal weightValue,
        String weightUnit,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal totalValue,
        String totalUnit
) {

    public static Object[] toArray(final WiwaOrderDto wiwaOrderDto) {
        return new Object[]{
                wiwaOrderDto.id,
                wiwaOrderDto.creator,
                wiwaOrderDto.created,
                wiwaOrderDto.status,
                wiwaOrderDto.orderNumber,
                wiwaOrderDto.description,
                wiwaOrderDto.weightValue,
                wiwaOrderDto.weightUnit,
                wiwaOrderDto.netWeightValue,
                wiwaOrderDto.netWeightUnit,
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
                (Long) array[4],
                (String) array[5],
                (BigDecimal) array[6],
                (String) array[7],
                (BigDecimal) array[8],
                (String) array[9],
                (BigDecimal) array[10],
                (String) array[11]
        );
    }
}
