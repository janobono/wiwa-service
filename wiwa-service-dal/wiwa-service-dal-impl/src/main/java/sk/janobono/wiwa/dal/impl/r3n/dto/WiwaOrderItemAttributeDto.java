package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderItemAttributeDto(
        Long orderItemId,
        String attributeKey,
        String attributeValue
) {

    public static Object[] toArray(final WiwaOrderItemAttributeDto wiwaOrderItemAttributeDto) {
        return new Object[]{
                wiwaOrderItemAttributeDto.orderItemId,
                wiwaOrderItemAttributeDto.attributeKey,
                wiwaOrderItemAttributeDto.attributeValue
        };
    }

    public static WiwaOrderItemAttributeDto toObject(final Object[] array) {
        return new WiwaOrderItemAttributeDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
