package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaBoardDto(
        Long id,
        String code,
        String boardCode,
        String structureCode,
        String name,
        String description,
        Boolean orientation,
        BigDecimal saleValue,
        String saleUnit,
        BigDecimal weightValue,
        String weightUnit,
        BigDecimal netWeightValue,
        String netWeightUnit,
        BigDecimal lengthValue,
        String lengthUnit,
        BigDecimal widthValue,
        String widthUnit,
        BigDecimal thicknessValue,
        String thicknessUnit,
        BigDecimal priceValue,
        String priceUnit
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
                wiwaBoardDto.saleValue,
                wiwaBoardDto.saleUnit,
                wiwaBoardDto.weightValue,
                wiwaBoardDto.weightUnit,
                wiwaBoardDto.netWeightValue,
                wiwaBoardDto.netWeightUnit,
                wiwaBoardDto.lengthValue,
                wiwaBoardDto.lengthUnit,
                wiwaBoardDto.widthValue,
                wiwaBoardDto.widthUnit,
                wiwaBoardDto.thicknessValue,
                wiwaBoardDto.thicknessUnit,
                wiwaBoardDto.priceValue,
                wiwaBoardDto.priceUnit
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
                (String) array[8],
                (BigDecimal) array[9],
                (String) array[10],
                (BigDecimal) array[11],
                (String) array[12],
                (BigDecimal) array[13],
                (String) array[14],
                (BigDecimal) array[15],
                (String) array[16],
                (BigDecimal) array[17],
                (String) array[18],
                (BigDecimal) array[19],
                (String) array[20]
        );
    }
}
