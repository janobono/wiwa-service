package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.user.UserCreateWebDto;
import sk.janobono.wiwa.api.model.user.UserProfileWebDto;
import sk.janobono.wiwa.api.model.user.UserWebDto;
import sk.janobono.wiwa.business.model.user.UserCreateData;
import sk.janobono.wiwa.business.model.user.UserProfileData;
import sk.janobono.wiwa.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserWebMapper {
    UserWebDto mapToWebDto(User user);

    UserCreateData mapToData(UserCreateWebDto userCreate);

    UserProfileData mapToData(UserProfileWebDto userProfile);
}
