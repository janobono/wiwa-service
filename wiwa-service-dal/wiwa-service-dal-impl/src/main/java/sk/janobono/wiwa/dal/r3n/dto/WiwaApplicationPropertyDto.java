package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaApplicationPropertyDto(
        String propertyGroup,
        String propertyKey,
        String propertyLanguage,
        String propertyValue
) {

    public static Object[] toArray(WiwaApplicationPropertyDto wiwaApplicationPropertyDto) {
        return new Object[]{
                wiwaApplicationPropertyDto.propertyGroup,
                wiwaApplicationPropertyDto.propertyKey,
                wiwaApplicationPropertyDto.propertyLanguage,
                wiwaApplicationPropertyDto.propertyValue
        };
    }

    public static WiwaApplicationPropertyDto toObject(Object[] array) {
        return new WiwaApplicationPropertyDto(
                (String) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
