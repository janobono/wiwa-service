package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaQuantityUnitDto(
        Long id,
        String type,
        String name,
        String unit
) {

    public static Object[] toArray(final WiwaQuantityUnitDto wiwaQuantityUnitDto) {
        return new Object[]{
                wiwaQuantityUnitDto.id,
                wiwaQuantityUnitDto.type,
                wiwaQuantityUnitDto.name,
                wiwaQuantityUnitDto.unit
        };
    }

    public static WiwaQuantityUnitDto toObject(final Object[] array) {
        return new WiwaQuantityUnitDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
