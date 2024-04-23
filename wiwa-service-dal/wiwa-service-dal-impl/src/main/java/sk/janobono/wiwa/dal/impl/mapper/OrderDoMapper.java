package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderDoMapper {

    OrderDo toOrderDo(WiwaOrderDto wiwaOrderDto);

    WiwaOrderDto toWiwaOrderDto(OrderDo orderDo);
}
