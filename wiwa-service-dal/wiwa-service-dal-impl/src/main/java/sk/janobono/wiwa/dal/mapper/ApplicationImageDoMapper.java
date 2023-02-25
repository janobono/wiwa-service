package sk.janobono.wiwa.dal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageDoMapper {
    ApplicationImageDo mapToDo(WiwaApplicationImageDto source);

    WiwaApplicationImageDto mapToDto(ApplicationImageDo source);
}
