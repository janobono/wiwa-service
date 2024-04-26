package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaOrderSummaryViewDto(
        Long id,
        String code,
        BigDecimal amount
) {

    public static Object[] toArray(final WiwaOrderSummaryViewDto wiwaOrderSummaryViewDto) {
        return new Object[]{
                wiwaOrderSummaryViewDto.id,
                wiwaOrderSummaryViewDto.code,
                wiwaOrderSummaryViewDto.amount
        };
    }

    public static WiwaOrderSummaryViewDto toObject(final Object[] array) {
        return new WiwaOrderSummaryViewDto(
                (Long) array[0],
                (String) array[1],
                (BigDecimal) array[2]
        );
    }
}
