package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.QuantityWebDto;
import sk.janobono.wiwa.model.Quantity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface QuantityWebMapper {

    QuantityWebDto mapToWebDto(Quantity quantity);

    Quantity mapToData(QuantityWebDto quantity);
}
