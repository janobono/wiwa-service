package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductImageDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductImageDoMapper {

    ProductImageDo toProductImageDo(WiwaProductImageDto wiwaProductImageDto);

    WiwaProductImageDto toWiwaProductImageDto(ProductImageDo productImageDo);
}
