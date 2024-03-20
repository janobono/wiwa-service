package sk.janobono.wiwa.dal.r3n.dto;

import java.math.BigDecimal;

public record WiwaFreeSaleProductViewDto(
        Long id,
        String code,
        String name,
        String description,
        String stockStatus,
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

    public static Object[] toArray(final WiwaFreeSaleProductViewDto wiwaFreeSaleProductViewDto) {
        return new Object[]{
                wiwaFreeSaleProductViewDto.id,
                wiwaFreeSaleProductViewDto.code,
                wiwaFreeSaleProductViewDto.name,
                wiwaFreeSaleProductViewDto.description,
                wiwaFreeSaleProductViewDto.stockStatus,
                wiwaFreeSaleProductViewDto.saleValue,
                wiwaFreeSaleProductViewDto.saleUnit,
                wiwaFreeSaleProductViewDto.weightValue,
                wiwaFreeSaleProductViewDto.weightUnit,
                wiwaFreeSaleProductViewDto.netWeightValue,
                wiwaFreeSaleProductViewDto.netWeightUnit,
                wiwaFreeSaleProductViewDto.lengthValue,
                wiwaFreeSaleProductViewDto.lengthUnit,
                wiwaFreeSaleProductViewDto.widthValue,
                wiwaFreeSaleProductViewDto.widthUnit,
                wiwaFreeSaleProductViewDto.thicknessValue,
                wiwaFreeSaleProductViewDto.thicknessUnit,
                wiwaFreeSaleProductViewDto.priceValue,
                wiwaFreeSaleProductViewDto.priceUnit
        };
    }

    public static WiwaFreeSaleProductViewDto toObject(final Object[] array) {
        return new WiwaFreeSaleProductViewDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (BigDecimal) array[5],
                (String) array[6],
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
                (String) array[18]
        );
    }
}
