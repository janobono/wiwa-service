package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.edge.*;
import sk.janobono.wiwa.business.model.edge.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {ApplicationImageWebMapper.class, QuantityWebMapper.class})
public interface EdgeWebMapper {

    EdgeCategoryWebDto mapToWebDto(EdgeCategoryData edgeCategory);

    EdgeCategoryItemWebDto mapToWebDto(EdgeCategoryItemData edgeCategoryItem);

    EdgeWebDto mapToWebDto(EdgeData edgeData);

    EdgeChangeData mapToData(EdgeChangeWebDto edgeChange);

    EdgeCategoryItemChangeData mapToData(EdgeCategoryItemChangeWebDto edgeCategoryItemChange);
}
