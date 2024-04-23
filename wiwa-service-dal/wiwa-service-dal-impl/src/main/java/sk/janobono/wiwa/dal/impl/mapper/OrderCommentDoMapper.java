package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderCommentDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderCommentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderCommentDoMapper {

    OrderCommentDo toOrderCommentDo(WiwaOrderCommentDto wiwaOrderCommentDto);

    WiwaOrderCommentDto toWiwaOrderCommentDto(OrderCommentDo orderCommentDo);
}
