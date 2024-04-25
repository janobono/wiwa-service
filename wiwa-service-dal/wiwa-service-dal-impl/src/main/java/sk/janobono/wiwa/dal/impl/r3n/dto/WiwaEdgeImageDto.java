package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaEdgeImageDto(
        Long edgeId,
        String fileType,
        byte[] data
) {

    public static Object[] toArray(final WiwaEdgeImageDto wiwaEdgeImageDto) {
        return new Object[]{
                wiwaEdgeImageDto.edgeId,
                wiwaEdgeImageDto.fileType,
                wiwaEdgeImageDto.data
        };
    }

    public static WiwaEdgeImageDto toObject(final Object[] array) {
        return new WiwaEdgeImageDto(
                (Long) array[0],
                (String) array[1],
                (byte[]) array[2]
        );
    }
}
