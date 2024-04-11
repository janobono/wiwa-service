package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaOrderItemDataDto(
        Long id,
        Long orderItemId,
        String key,
        String data
) {

    public static Object[] toArray(final WiwaOrderItemDataDto wiwaOrderItemDataDto) {
        return new Object[]{
                wiwaOrderItemDataDto.id,
                wiwaOrderItemDataDto.orderItemId,
                wiwaOrderItemDataDto.key,
                wiwaOrderItemDataDto.data
        };
    }

    public static WiwaOrderItemDataDto toObject(final Object[] array) {
        return new WiwaOrderItemDataDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
