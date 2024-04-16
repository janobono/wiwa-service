package sk.janobono.wiwa.dal.impl.r3n.dto;

public record WiwaUserDto(
        Long id,
        String username,
        String password,
        String titleBefore,
        String firstName,
        String midName,
        String lastName,
        String titleAfter,
        String email,
        Boolean gdpr,
        Boolean confirmed,
        Boolean enabled
) {

    public static Object[] toArray(final WiwaUserDto wiwaUserDto) {
        return new Object[]{
                wiwaUserDto.id,
                wiwaUserDto.username,
                wiwaUserDto.password,
                wiwaUserDto.titleBefore,
                wiwaUserDto.firstName,
                wiwaUserDto.midName,
                wiwaUserDto.lastName,
                wiwaUserDto.titleAfter,
                wiwaUserDto.email,
                wiwaUserDto.gdpr,
                wiwaUserDto.confirmed,
                wiwaUserDto.enabled
        };
    }

    public static WiwaUserDto toObject(final Object[] array) {
        return new WiwaUserDto(
                (Long) array[0],
                (String) array[1],
                (String) array[2],
                (String) array[3],
                (String) array[4],
                (String) array[5],
                (String) array[6],
                (String) array[7],
                (String) array[8],
                (Boolean) array[9],
                (Boolean) array[10],
                (Boolean) array[11]
        );
    }
}
