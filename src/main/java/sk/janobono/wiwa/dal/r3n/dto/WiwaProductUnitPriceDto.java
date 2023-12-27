package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WiwaProductUnitPriceDto(
        Long id,
        Long productId,
        Long unitId,
        LocalDate validFrom,
        LocalDate validTo,
        BigDecimal value
) {

    public static Object[] toArray(final WiwaProductUnitPriceDto wiwaProductUnitPriceDto) {
        return new Object[]{
                wiwaProductUnitPriceDto.id,
                wiwaProductUnitPriceDto.productId,
                wiwaProductUnitPriceDto.unitId,
                wiwaProductUnitPriceDto.validFrom,
                wiwaProductUnitPriceDto.validTo,
                wiwaProductUnitPriceDto.value
        };
    }

    public static WiwaProductUnitPriceDto toObject(final Object[] array) {
        return new WiwaProductUnitPriceDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (LocalDate) array[3],
                (LocalDate) array[4],
                (BigDecimal) array[5]
        );
    }
}
