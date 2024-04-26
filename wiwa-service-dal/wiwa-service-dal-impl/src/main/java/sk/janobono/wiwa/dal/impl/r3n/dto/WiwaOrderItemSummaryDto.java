package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderItemSummaryDto(
        Long orderItemId,
        String code,
        BigDecimal amount
) {

    public static Object[] toArray(final WiwaOrderItemSummaryDto wiwaOrderItemSummaryDto) {
        return new Object[]{
                wiwaOrderItemSummaryDto.orderItemId,
                wiwaOrderItemSummaryDto.code,
                wiwaOrderItemSummaryDto.amount
        };
    }

    public static WiwaOrderItemSummaryDto toObject(final Object[] array) {
        return new WiwaOrderItemSummaryDto(
                (Long) array[0],
                (String) array[1],
                (BigDecimal) array[2]
        );
    }
}
