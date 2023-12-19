package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaProductImageDto(
        Long id,
        Long productId,
        String fileName,
        String fileType,
        byte[] thumbnail,
        byte[] data
) {

    public static Object[] toArray(final WiwaProductImageDto wiwaProductImageDto) {
        return new Object[]{
                wiwaProductImageDto.id,
                wiwaProductImageDto.productId,
                wiwaProductImageDto.fileName,
                wiwaProductImageDto.fileType,
                wiwaProductImageDto.thumbnail,
                wiwaProductImageDto.data
        };
    }

    public static WiwaProductImageDto toObject(final Object[] array) {
        return new WiwaProductImageDto(
                (Long) array[0],
                (Long) array[1],
                (String) array[2],
                (String) array[3],
                (byte[]) array[4],
                (byte[]) array[5]
        );
    }
}
