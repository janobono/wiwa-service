package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderMaterialDto(
        Long orderId,
        Long materialId,
        String code,
        String data
) {

    public static Object[] toArray(final WiwaOrderMaterialDto wiwaOrderMaterialDto) {
        return new Object[]{
                wiwaOrderMaterialDto.orderId,
                wiwaOrderMaterialDto.materialId,
                wiwaOrderMaterialDto.code,
                wiwaOrderMaterialDto.data
        };
    }

    public static WiwaOrderMaterialDto toObject(final Object[] array) {
        return new WiwaOrderMaterialDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
