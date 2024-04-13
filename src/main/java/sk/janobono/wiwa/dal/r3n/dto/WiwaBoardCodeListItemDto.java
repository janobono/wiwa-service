package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaBoardCodeListItemDto(
        Long boardId,
        Long codeListItemId
) {

    public static Object[] toArray(final WiwaBoardCodeListItemDto wiwaBoardCodeListItemDto) {
        return new Object[]{
                wiwaBoardCodeListItemDto.boardId,
                wiwaBoardCodeListItemDto.codeListItemId
        };
    }

    public static WiwaBoardCodeListItemDto toObject(final Object[] array) {
        return new WiwaBoardCodeListItemDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
