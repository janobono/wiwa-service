package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaApplicationPropertyDto(
        String propertyKey,
        String propertyValue
) {

    public static Object[] toArray(final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) {
        return new Object[]{
                wiwaApplicationPropertyDto.propertyKey,
                wiwaApplicationPropertyDto.propertyValue
        };
    }

    public static WiwaApplicationPropertyDto toObject(final Object[] array) {
        return new WiwaApplicationPropertyDto(
                (String) array[0],
                (String) array[1]
        );
    }
}
