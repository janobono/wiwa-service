package sk.janobono.wiwa.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.common.model.UserSo;
import sk.janobono.wiwa.dal.domain.UserDo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserSo mapToSo(UserDo source);
}
