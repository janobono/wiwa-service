package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderMaterialDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderMaterialDoMapper {

    OrderMaterialDo toOrderMaterialDo(WiwaOrderMaterialDto wiwaOrderMaterialDto);

    WiwaOrderMaterialDto toWiwaOrderMaterialDto(OrderMaterialDo orderMaterialDo);
}
