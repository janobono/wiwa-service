package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WiwaProductUnitPriceDto(
        Long id,
        Long productId,
        LocalDate validFrom,
        LocalDate validTo,
        BigDecimal value,
        String unit
) {

    public static Object[] toArray(final WiwaProductUnitPriceDto wiwaProductUnitPriceDto) {
        return new Object[]{
                wiwaProductUnitPriceDto.id,
                wiwaProductUnitPriceDto.productId,
                wiwaProductUnitPriceDto.validFrom,
                wiwaProductUnitPriceDto.validTo,
                wiwaProductUnitPriceDto.value,
                wiwaProductUnitPriceDto.unit
        };
    }

    public static WiwaProductUnitPriceDto toObject(final Object[] array) {
        return new WiwaProductUnitPriceDto(
                (Long) array[0],
                (Long) array[1],
                (LocalDate) array[2],
                (LocalDate) array[3],
                (BigDecimal) array[4],
                (String) array[5]
        );
    }
}
