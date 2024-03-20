package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaBoardProductViewDto(
        Long id,
        String code,
        String name,
        String description,
        String stockStatus,
        String boardCode,
        String structureCode,
        String orientation,
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

    public static Object[] toArray(final WiwaBoardProductViewDto wiwaBoardProductViewDto) {
        return new Object[]{
                wiwaBoardProductViewDto.id,
                wiwaBoardProductViewDto.code,
                wiwaBoardProductViewDto.name,
                wiwaBoardProductViewDto.description,
                wiwaBoardProductViewDto.stockStatus,
                wiwaBoardProductViewDto.boardCode,
                wiwaBoardProductViewDto.structureCode,
                wiwaBoardProductViewDto.orientation,
                wiwaBoardProductViewDto.saleValue,
                wiwaBoardProductViewDto.saleUnit,
                wiwaBoardProductViewDto.weightValue,
                wiwaBoardProductViewDto.weightUnit,
                wiwaBoardProductViewDto.netWeightValue,
                wiwaBoardProductViewDto.netWeightUnit,
                wiwaBoardProductViewDto.lengthValue,
                wiwaBoardProductViewDto.lengthUnit,
                wiwaBoardProductViewDto.widthValue,
                wiwaBoardProductViewDto.widthUnit,
                wiwaBoardProductViewDto.thicknessValue,
                wiwaBoardProductViewDto.thicknessUnit,
                wiwaBoardProductViewDto.priceValue,
                wiwaBoardProductViewDto.priceUnit
        };
    }

    public static WiwaBoardProductViewDto toObject(final Object[] array) {
        return new WiwaBoardProductViewDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (String) array[6],
                (String) array[7],
                (BigDecimal) array[8],
                (String) array[9],
                (BigDecimal) array[10],
                (String) array[11],
                (BigDecimal) array[12],
                (String) array[13],
                (BigDecimal) array[14],
                (String) array[15],
                (BigDecimal) array[16],
                (String) array[17],
                (BigDecimal) array[18],
                (String) array[19],
                (BigDecimal) array[20],
                (String) array[21]
        );
    }
}
