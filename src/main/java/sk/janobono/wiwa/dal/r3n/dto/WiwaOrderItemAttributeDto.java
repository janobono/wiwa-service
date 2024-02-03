package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaOrderItemAttributeDto(
        Long id,
        Long orderItemId,
        String key,
        String value
) {

    public static Object[] toArray(final WiwaOrderItemAttributeDto wiwaOrderItemAttributeDto) {
        return new Object[]{
                wiwaOrderItemAttributeDto.id,
                wiwaOrderItemAttributeDto.orderItemId,
                wiwaOrderItemAttributeDto.key,
                wiwaOrderItemAttributeDto.value
        };
    }

    public static WiwaOrderItemAttributeDto toObject(final Object[] array) {
        return new WiwaOrderItemAttributeDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
