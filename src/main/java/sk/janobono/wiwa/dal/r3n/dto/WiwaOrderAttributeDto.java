package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaOrderAttributeDto(
        Long orderId,
        String attributeKey,
        String attributeValue
) {

    public static Object[] toArray(final WiwaOrderAttributeDto wiwaOrderAttributeDto) {
        return new Object[]{
                wiwaOrderAttributeDto.orderId,
                wiwaOrderAttributeDto.attributeKey,
                wiwaOrderAttributeDto.attributeValue
        };
    }

    public static WiwaOrderAttributeDto toObject(final Object[] array) {
        return new WiwaOrderAttributeDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2]
        );
    }
}
