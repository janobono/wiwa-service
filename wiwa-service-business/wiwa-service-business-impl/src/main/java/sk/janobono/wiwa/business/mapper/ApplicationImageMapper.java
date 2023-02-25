package sk.janobono.wiwa.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageMapper {
    ApplicationImageSo map(ApplicationImageInfoDo source);
}
