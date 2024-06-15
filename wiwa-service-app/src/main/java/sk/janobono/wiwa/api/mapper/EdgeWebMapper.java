package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.edge.EdgeChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeWebDto;
import sk.janobono.wiwa.business.model.edge.EdgeChangeData;
import sk.janobono.wiwa.business.model.edge.EdgeData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {CategoryWebMapper.class})
public interface EdgeWebMapper {

    EdgeWebDto mapToWebDto(EdgeData edgeData);

    EdgeChangeData mapToData(EdgeChangeWebDto edgeChange);
}
