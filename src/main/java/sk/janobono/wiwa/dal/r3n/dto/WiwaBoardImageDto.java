package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaBoardImageDto(
        Long id,
        Long boardId,
        String fileName,
        String fileType,
        byte[] thumbnail,
        byte[] data
) {

    public static Object[] toArray(final WiwaBoardImageDto wiwaBoardImageDto) {
        return new Object[]{
                wiwaBoardImageDto.id,
                wiwaBoardImageDto.boardId,
                wiwaBoardImageDto.fileName,
                wiwaBoardImageDto.fileType,
                wiwaBoardImageDto.thumbnail,
                wiwaBoardImageDto.data
        };
    }

    public static WiwaBoardImageDto toObject(final Object[] array) {
        return new WiwaBoardImageDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3],
                (byte[]) array[4],
                (byte[]) array[5]
        );
    }
}
