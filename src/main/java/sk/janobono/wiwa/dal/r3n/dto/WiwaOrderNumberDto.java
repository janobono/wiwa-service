package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaOrderNumberDto(
        String creator,
        Long orderNumber
) {

    public static Object[] toArray(final WiwaOrderNumberDto wiwaOrderNumberDto) {
        return new Object[]{
                wiwaOrderNumberDto.creator,
                wiwaOrderNumberDto.orderNumber
        };
    }

    public static WiwaOrderNumberDto toObject(final Object[] array) {
        return new WiwaOrderNumberDto(
                (String) array[0],
                (Long) array[1]
        );
    }
}
