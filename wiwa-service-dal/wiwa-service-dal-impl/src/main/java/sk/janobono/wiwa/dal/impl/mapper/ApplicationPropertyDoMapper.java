package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaApplicationPropertyDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationPropertyDoMapper {

    @Mapping(source = "propertyKey", target = "key")
    @Mapping(source = "propertyValue", target = "value")
    ApplicationPropertyDo toApplicationPropertyDo(WiwaApplicationPropertyDto wiwaApplicationPropertyDto);

    @Mapping(source = "key", target = "propertyKey")
    @Mapping(source = "value", target = "propertyValue")
    WiwaApplicationPropertyDto toWiwaApplicationPropertyDto(ApplicationPropertyDo applicationPropertyDo);
}
