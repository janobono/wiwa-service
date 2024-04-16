package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderNumberDto(
        Long userId,
        Long orderNumber
) {

    public static Object[] toArray(final WiwaOrderNumberDto wiwaOrderNumberDto) {
        return new Object[]{
                wiwaOrderNumberDto.userId,
                wiwaOrderNumberDto.orderNumber
        };
    }

    public static WiwaOrderNumberDto toObject(final Object[] array) {
        return new WiwaOrderNumberDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
