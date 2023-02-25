package sk.janobono.wiwa.dal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaUserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserDoMapper {

    WiwaUserDto mapToDto(UserDo source);
}
