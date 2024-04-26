package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderItemDto(
        Long id,
        Long orderId,
        Integer sortNum,
        String name,
        Integer quantity,
        String part,
        String summary
) {

    public static Object[] toArray(final WiwaOrderItemDto wiwaOrderItemDto) {
        return new Object[]{
                wiwaOrderItemDto.id,
                wiwaOrderItemDto.orderId,
                wiwaOrderItemDto.sortNum,
                wiwaOrderItemDto.name,
                wiwaOrderItemDto.quantity,
                wiwaOrderItemDto.part,
                wiwaOrderItemDto.summary
        };
    }

    public static WiwaOrderItemDto toObject(final Object[] array) {
        return new WiwaOrderItemDto(
                (Long) array[0],
                (Long) array[1],
                (Integer) array[2],
                (String) array[3],
                (Integer) array[4],
                (String) array[5],
                (String) array[6]
        );
    }
}
