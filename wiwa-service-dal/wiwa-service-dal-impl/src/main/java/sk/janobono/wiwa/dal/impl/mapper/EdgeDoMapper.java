package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface EdgeDoMapper {

    EdgeDo toEdgeDo(WiwaEdgeDto wiwaEdgeDto);

    WiwaEdgeDto toWiwaEdgeDto(EdgeDo edgeDo);
}
