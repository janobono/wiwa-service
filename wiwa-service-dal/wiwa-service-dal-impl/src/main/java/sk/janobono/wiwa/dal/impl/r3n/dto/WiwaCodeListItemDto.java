package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaCodeListItemDto(
        Long id,
        Long codeListId,
        Long parentId,
        String treeCode,
        String code,
        String value,
        Integer sortNum
) {

    public static Object[] toArray(final WiwaCodeListItemDto wiwaCodeListItemDto) {
        return new Object[]{
                wiwaCodeListItemDto.id,
                wiwaCodeListItemDto.codeListId,
                wiwaCodeListItemDto.parentId,
                wiwaCodeListItemDto.treeCode,
                wiwaCodeListItemDto.code,
                wiwaCodeListItemDto.value,
                wiwaCodeListItemDto.sortNum
        };
    }

    public static WiwaCodeListItemDto toObject(final Object[] array) {
        return new WiwaCodeListItemDto(
                (Long) array[0],
                (Long) array[1],
                (Long) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (Integer) array[6]
        );
    }
}
