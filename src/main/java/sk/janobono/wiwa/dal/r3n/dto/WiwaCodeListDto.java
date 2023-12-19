package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaCodeListDto(
        Long id,
        String code,
        String name
) {

    public static Object[] toArray(final WiwaCodeListDto wiwaCodeListDto) {
        return new Object[]{
                wiwaCodeListDto.id,
                wiwaCodeListDto.code,
                wiwaCodeListDto.name
        };
    }

    public static WiwaCodeListDto toObject(final Object[] array) {
        return new WiwaCodeListDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
