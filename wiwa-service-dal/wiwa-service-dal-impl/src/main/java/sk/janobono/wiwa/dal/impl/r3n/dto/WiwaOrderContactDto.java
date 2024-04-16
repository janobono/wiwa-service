package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaOrderContactDto(
        Long orderId,
        String name,
        String street,
        String zipCode,
        String city,
        String state,
        String phone,
        String email,
        String businessId,
        String taxId
) {

    public static Object[] toArray(final WiwaOrderContactDto wiwaOrderContactDto) {
        return new Object[]{
                wiwaOrderContactDto.orderId,
                wiwaOrderContactDto.name,
                wiwaOrderContactDto.street,
                wiwaOrderContactDto.zipCode,
                wiwaOrderContactDto.city,
                wiwaOrderContactDto.state,
                wiwaOrderContactDto.phone,
                wiwaOrderContactDto.email,
                wiwaOrderContactDto.businessId,
                wiwaOrderContactDto.taxId
        };
    }

    public static WiwaOrderContactDto toObject(final Object[] array) {
        return new WiwaOrderContactDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (String) array[6],
                (String) array[7],
                (String) array[8],
                (String) array[9]
        );
    }
}
