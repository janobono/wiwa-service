package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaProductDto(
        Long id,
        String code,
        String name,
        String description,
        String stockStatus
) {

    public static Object[] toArray(final WiwaProductDto wiwaProductDto) {
        return new Object[]{
                wiwaProductDto.id,
                wiwaProductDto.code,
                wiwaProductDto.name,
                wiwaProductDto.description,
                wiwaProductDto.stockStatus
        };
    }

    public static WiwaProductDto toObject(final Object[] array) {
        return new WiwaProductDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4]
        );
    }
}
