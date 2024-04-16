package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaUserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserDoMapper {

    UserDo toUserDo(WiwaUserDto wiwaUserDto);

    WiwaUserDto toWiwaUserDto(UserDo userDo);
}
