package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.model.OrderPackageType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderPackageType.class})
public interface OrderDoMapper {

    @Mapping(target = "packageType", expression = "java(OrderPackageType.valueOf(wiwaOrderDto.packageType()))")
    OrderDo toOrderDo(WiwaOrderDto wiwaOrderDto);

    @Mapping(target = "packageType", expression = "java(orderDo.getPackageType().name())")
    WiwaOrderDto toWiwaOrderDto(OrderDo orderDo);
}
