package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaAuthorityDto;
import sk.janobono.wiwa.model.Authority;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {Authority.class})
public interface AuthorityMapper {

    @Mapping(target = "authority", expression = "java(Authority.byValue(wiwaAuthorityDto.authority()))")
    AuthorityDo toAuthorityDo(WiwaAuthorityDto wiwaAuthorityDto);

    @Mapping(target = "authority", expression = "java(authorityDo.getAuthority().toString())")
    WiwaAuthorityDto toWiwaAuthorityDto(AuthorityDo authorityDo);
}
