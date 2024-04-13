package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaEdgeImageDto(
        Long id,
        Long edgeId,
        String fileName,
        String fileType,
        byte[] thumbnail,
        byte[] data
) {

    public static Object[] toArray(final WiwaEdgeImageDto wiwaEdgeImageDto) {
        return new Object[]{
                wiwaEdgeImageDto.id,
                wiwaEdgeImageDto.edgeId,
                wiwaEdgeImageDto.fileName,
                wiwaEdgeImageDto.fileType,
                wiwaEdgeImageDto.thumbnail,
                wiwaEdgeImageDto.data
        };
    }

    public static WiwaEdgeImageDto toObject(final Object[] array) {
        return new WiwaEdgeImageDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3],
                (byte[]) array[4],
                (byte[]) array[5]
        );
    }
}
