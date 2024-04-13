package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaEdgeCodeListItemDto(
        Long edgeId,
        Long codeListItemId
) {

    public static Object[] toArray(final WiwaEdgeCodeListItemDto wiwaEdgeCodeListItemDto) {
        return new Object[]{
                wiwaEdgeCodeListItemDto.edgeId,
                wiwaEdgeCodeListItemDto.codeListItemId
        };
    }

    public static WiwaEdgeCodeListItemDto toObject(final Object[] array) {
        return new WiwaEdgeCodeListItemDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
