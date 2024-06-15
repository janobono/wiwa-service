package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.CategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.CategoryItemWebDto;
import sk.janobono.wiwa.api.model.CategoryWebDto;
import sk.janobono.wiwa.business.model.CategoryData;
import sk.janobono.wiwa.business.model.CategoryItemChangeData;
import sk.janobono.wiwa.business.model.CategoryItemData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CategoryWebMapper {

    CategoryWebDto mapToWebDto(CategoryData category);

    CategoryItemWebDto mapToWebDto(CategoryItemData categoryItem);

    CategoryItemChangeData mapToData(CategoryItemChangeWebDto categoryItemChange);
}
