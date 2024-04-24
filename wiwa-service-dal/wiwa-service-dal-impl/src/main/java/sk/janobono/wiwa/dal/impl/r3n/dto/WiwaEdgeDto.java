package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaEdgeDto(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {

    public static Object[] toArray(final WiwaEdgeDto wiwaEdgeDto) {
        return new Object[]{
                wiwaEdgeDto.id,
                wiwaEdgeDto.code,
                wiwaEdgeDto.name,
                wiwaEdgeDto.description,
                wiwaEdgeDto.weight,
                wiwaEdgeDto.width,
                wiwaEdgeDto.thickness,
                wiwaEdgeDto.price
        };
    }

    public static WiwaEdgeDto toObject(final Object[] array) {
        return new WiwaEdgeDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (BigDecimal) array[4],
                (BigDecimal) array[5],
                (BigDecimal) array[6],
                (BigDecimal) array[7]
        );
    }
}
