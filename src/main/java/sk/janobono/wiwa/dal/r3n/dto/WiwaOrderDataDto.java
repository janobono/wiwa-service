package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaOrderDataDto(
        Long id,
        Long orderId,
        String key,
        String data
) {

    public static Object[] toArray(final WiwaOrderDataDto wiwaOrderDataDto) {
        return new Object[]{
                wiwaOrderDataDto.id,
                wiwaOrderDataDto.orderId,
                wiwaOrderDataDto.key,
                wiwaOrderDataDto.data
        };
    }

    public static WiwaOrderDataDto toObject(final Object[] array) {
        return new WiwaOrderDataDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
