package sk.janobono.wiwa.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.business.model.ApplicationImageData;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.domain.ProductImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageDataMapper {
    ApplicationImageInfoData mapToData(ApplicationImageInfoDo applicationImageInfo);
    ApplicationImageInfoData mapToInfoData(ApplicationImageDo applicationImage);

    ApplicationImageData mapToData(ApplicationImageDo applicationImage);

    ApplicationImageData mapToData(ProductImageDo productImage);
}
