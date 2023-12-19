package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductUnitPriceDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductUnitPriceDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProductUnitPriceMapper {

    ProductUnitPriceDo toProductUnitPriceDo(WiwaProductUnitPriceDto wiwaProductUnitPriceDto);

    WiwaProductUnitPriceDto toWiwaProductUnitPriceDto(ProductUnitPriceDo productUnitPriceDo);
}
