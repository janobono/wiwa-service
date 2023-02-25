package sk.janobono.wiwa.dal.r3n.dto;

public record WiwaUserAuthorityDto(
        Long userId,
        Long authorityId
) {

    public static Object[] toArray(WiwaUserAuthorityDto wiwaUserAuthorityDto) {
        return new Object[]{
                wiwaUserAuthorityDto.userId,
                wiwaUserAuthorityDto.authorityId
        };
    }

    public static WiwaUserAuthorityDto toObject(Object[] array) {
        return new WiwaUserAuthorityDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
