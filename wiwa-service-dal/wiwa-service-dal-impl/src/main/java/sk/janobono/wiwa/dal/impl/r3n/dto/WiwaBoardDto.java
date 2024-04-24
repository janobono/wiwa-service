package sk.janobono.wiwa.dal.impl.r3n.dto;

import java.math.BigDecimal;

public record WiwaBoardDto(
        Long id,
        String code,
        String boardCode,
        String structureCode,
        String name,
        String description,
        Boolean orientation,
        BigDecimal weight,
        BigDecimal length,
        BigDecimal width,
        BigDecimal thickness,
        BigDecimal price
) {

    public static Object[] toArray(final WiwaBoardDto wiwaBoardDto) {
        return new Object[]{
                wiwaBoardDto.id,
                wiwaBoardDto.code,
                wiwaBoardDto.boardCode,
                wiwaBoardDto.structureCode,
                wiwaBoardDto.name,
                wiwaBoardDto.description,
                wiwaBoardDto.orientation,
                wiwaBoardDto.weight,
                wiwaBoardDto.length,
                wiwaBoardDto.width,
                wiwaBoardDto.thickness,
                wiwaBoardDto.price
        };
    }

    public static WiwaBoardDto toObject(final Object[] array) {
        return new WiwaBoardDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (Boolean) array[6],
                (BigDecimal) array[7],
                (BigDecimal) array[8],
                (BigDecimal) array[9],
                (BigDecimal) array[10],
                (BigDecimal) array[11]
        );
    }
}
