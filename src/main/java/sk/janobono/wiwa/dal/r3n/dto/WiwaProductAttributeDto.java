package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaProductAttributeDto(
        Long id,
        Long productId,
        String key,
        String value
) {

    public static Object[] toArray(final WiwaProductAttributeDto wiwaProductAttributeDto) {
        return new Object[]{
                wiwaProductAttributeDto.id,
                wiwaProductAttributeDto.productId,
                wiwaProductAttributeDto.key,
                wiwaProductAttributeDto.value
        };
    }

    public static WiwaProductAttributeDto toObject(final Object[] array) {
        return new WiwaProductAttributeDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3]
        );
    }
}
