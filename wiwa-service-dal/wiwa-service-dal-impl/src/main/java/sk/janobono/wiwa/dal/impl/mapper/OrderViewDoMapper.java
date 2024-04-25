package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderViewDto;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderStatus.class, OrderPackageType.class})
public interface OrderViewDoMapper {

    @Mapping(target = "status", expression = "java(OrderStatus.valueOf(wiwaOrderViewDto.status()))")
    @Mapping(target = "packageType", expression = "java(OrderPackageType.valueOf(wiwaOrderViewDto.packageType()))")
    OrderViewDo toOrderViewDo(WiwaOrderViewDto wiwaOrderViewDto);
}
