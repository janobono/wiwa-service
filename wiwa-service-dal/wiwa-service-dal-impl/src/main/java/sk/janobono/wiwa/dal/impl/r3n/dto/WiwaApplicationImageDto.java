package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaApplicationImageDto(
        String fileName,
        String fileType,
        byte[] thumbnail,
        byte[] data
) {

    public static Object[] toArray(final WiwaApplicationImageDto wiwaApplicationImageDto) {
        return new Object[]{
                wiwaApplicationImageDto.fileName,
                wiwaApplicationImageDto.fileType,
                wiwaApplicationImageDto.thumbnail,
                wiwaApplicationImageDto.data
        };
    }

    public static WiwaApplicationImageDto toObject(final Object[] array) {
        return new WiwaApplicationImageDto(
                (String) array[0],
                (String) array[1],
                (byte[]) array[2],
                (byte[]) array[3]
        );
    }
}
