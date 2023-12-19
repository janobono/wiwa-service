package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaQuantityUnitDto(
        String id,
        String type,
        String unit
) {

    public static Object[] toArray(final WiwaQuantityUnitDto wiwaQuantityUnitDto) {
        return new Object[]{
                wiwaQuantityUnitDto.id,
                wiwaQuantityUnitDto.type,
                wiwaQuantityUnitDto.unit
        };
    }

    public static WiwaQuantityUnitDto toObject(final Object[] array) {
        return new WiwaQuantityUnitDto(
                (String) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
