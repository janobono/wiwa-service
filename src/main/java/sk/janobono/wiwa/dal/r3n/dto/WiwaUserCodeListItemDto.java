package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaUserCodeListItemDto(
        Long userId,
        Long codeListItemId
) {

    public static Object[] toArray(final WiwaUserCodeListItemDto wiwaUserCodeListItemDto) {
        return new Object[]{
                wiwaUserCodeListItemDto.userId,
                wiwaUserCodeListItemDto.codeListItemId
        };
    }

    public static WiwaUserCodeListItemDto toObject(final Object[] array) {
        return new WiwaUserCodeListItemDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
