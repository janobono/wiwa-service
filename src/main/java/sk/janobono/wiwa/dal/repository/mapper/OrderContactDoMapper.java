package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderContactDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderContactDoMapper {

    OrderContactDo toOrderContactDo(WiwaOrderContactDto wiwaOrderContactDto);

    WiwaOrderContactDto toWiwaOrderContactDto(OrderContactDo orderContactDo);
}
