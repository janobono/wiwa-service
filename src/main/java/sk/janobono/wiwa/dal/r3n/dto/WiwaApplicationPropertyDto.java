package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaApplicationPropertyDto(
        String propertyGroup,
        String propertyKey,
        String propertyValue
) {

    public static Object[] toArray(final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) {
        return new Object[]{
                wiwaApplicationPropertyDto.propertyGroup,
                wiwaApplicationPropertyDto.propertyKey,
                wiwaApplicationPropertyDto.propertyValue
        };
    }

    public static WiwaApplicationPropertyDto toObject(final Object[] array) {
        return new WiwaApplicationPropertyDto(
                (String) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
