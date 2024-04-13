package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaEdgeImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface EdgeImageDoMapper {

    EdgeImageDo toEdgeImageDo(WiwaEdgeImageDto wiwaEdgeImageDto);

    WiwaEdgeImageDto toWiwaEdgeImageDto(EdgeImageDo edgeImageDo);
}
