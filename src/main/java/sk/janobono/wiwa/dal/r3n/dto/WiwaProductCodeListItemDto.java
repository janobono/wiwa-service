package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaProductCodeListItemDto(
        Long productId,
        Long codeListItemId
) {

    public static Object[] toArray(final WiwaProductCodeListItemDto wiwaProductCodeListItemDto) {
        return new Object[]{
                wiwaProductCodeListItemDto.productId,
                wiwaProductCodeListItemDto.codeListItemId
        };
    }

    public static WiwaProductCodeListItemDto toObject(final Object[] array) {
        return new WiwaProductCodeListItemDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
