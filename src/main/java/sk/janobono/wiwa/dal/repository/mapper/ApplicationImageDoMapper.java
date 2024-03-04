package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageDoMapper {

    ApplicationImageDo toApplicationImageDo(WiwaApplicationImageDto wiwaApplicationImageDto);

    WiwaApplicationImageDto toWiwaApplicationImageDto(ApplicationImageDo applicationImageDo);
}
