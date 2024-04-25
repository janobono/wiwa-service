package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaBoardImageDto(
        Long boardId,
        String fileType,
        byte[] data
) {

    public static Object[] toArray(final WiwaBoardImageDto wiwaBoardImageDto) {
        return new Object[]{
                wiwaBoardImageDto.boardId,
                wiwaBoardImageDto.fileType,
                wiwaBoardImageDto.data
        };
    }

    public static WiwaBoardImageDto toObject(final Object[] array) {
        return new WiwaBoardImageDto(
                (Long) array[0],
                (String) array[1],
                (byte[]) array[2]
        );
    }
}
