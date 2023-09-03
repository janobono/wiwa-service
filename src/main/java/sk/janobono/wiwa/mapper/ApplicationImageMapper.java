package sk.janobono.wiwa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.domain.ProductImageDo;
import sk.janobono.wiwa.model.ApplicationImage;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageMapper {
    ApplicationImage map(ApplicationImageDo applicationImageDo);

    ApplicationImage map(ProductImageDo productImageDo);
}
