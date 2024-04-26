package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemSummaryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderItemSummaryDoMapper {

    OrderItemSummaryDo toOrderItemSummaryDo(WiwaOrderItemSummaryDto wiwaOrderItemSummaryDto);

    WiwaOrderItemSummaryDto toWiwaOrderItemSummaryDto(OrderItemSummaryDo orderItemSummaryDo);
}
