package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationPropertyDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationPropertyDoMapper {

    @Mapping(source = "propertyGroup", target = "group")
    @Mapping(source = "propertyKey", target = "key")
    @Mapping(source = "propertyValue", target = "value")
    ApplicationPropertyDo toApplicationPropertyDo(WiwaApplicationPropertyDto wiwaApplicationPropertyDto);

    @Mapping(source = "group", target = "propertyGroup")
    @Mapping(source = "key", target = "propertyKey")
    @Mapping(source = "value", target = "propertyValue")
    WiwaApplicationPropertyDto toWiwaApplicationPropertyDto(ApplicationPropertyDo applicationPropertyDo);
}
