package sk.janobono.wiwa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    User mapToUser(UserDo userDo);

    default Authority authorityDoToAuthority(final AuthorityDo authorityDo) {
        return authorityDo.getAuthority();
    }
}
