package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Integer sortNum,
        String data
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.data
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Integer) array[2],
                (String) array[3]
        );
    }
}
