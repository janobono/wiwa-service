package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaAuthorityDto(
        Long id,
        String authority
) {

    public static Object[] toArray(WiwaAuthorityDto wiwaAuthorityDto) {
        return new Object[]{
                wiwaAuthorityDto.id,
                wiwaAuthorityDto.authority
        };
    }

    public static WiwaAuthorityDto toObject(Object[] array) {
        return new WiwaAuthorityDto(
                (Long) array[0],
                (String) array[1]
        );
    }
}
