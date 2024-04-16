package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaUserAuthorityDto(
        Long userId,
        Long authorityId
) {

    public static Object[] toArray(final WiwaUserAuthorityDto wiwaUserAuthorityDto) {
        return new Object[]{
                wiwaUserAuthorityDto.userId,
                wiwaUserAuthorityDto.authorityId
        };
    }

    public static WiwaUserAuthorityDto toObject(final Object[] array) {
        return new WiwaUserAuthorityDto(
                (Long) array[0],
                (Long) array[1]
        );
    }
}
