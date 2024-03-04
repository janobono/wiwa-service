package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.codelist.CodeListItemChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListItemWebDto;
import sk.janobono.wiwa.business.model.codelist.CodeListItemChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodeListItemWebMapper {
    CodeListItemWebDto mapToWebDto(CodeListItemData codeListItem);

    CodeListItemChangeData mapToData(CodeListItemChangeWebDto codeListItemChange);
}
