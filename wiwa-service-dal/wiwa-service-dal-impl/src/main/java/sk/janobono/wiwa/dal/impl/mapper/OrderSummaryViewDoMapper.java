package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderSummaryViewDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderSummaryViewDoMapper {

    OrderSummaryViewDo toOrderSummaryViewDo(WiwaOrderSummaryViewDto wiwaOrderSummaryViewDto);
}
