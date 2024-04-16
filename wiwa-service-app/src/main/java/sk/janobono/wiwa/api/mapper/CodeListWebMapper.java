package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.codelist.CodeListChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListWebDto;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodeListWebMapper {
    CodeListWebDto mapToWebDto(CodeListData codeList);

    CodeListChangeData mapToData(CodeListChangeWebDto codeListChange);
}
