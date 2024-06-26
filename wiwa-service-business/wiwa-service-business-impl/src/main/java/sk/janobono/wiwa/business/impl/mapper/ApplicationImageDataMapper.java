package sk.janobono.wiwa.business.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.business.model.application.ApplicationImageData;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ApplicationImageDataMapper {

    ApplicationImageInfoData mapToData(ApplicationImageInfoDo applicationImageInfo);

    ApplicationImageInfoData mapToInfoData(ApplicationImageDo applicationImage);

    ApplicationImageData mapToData(ApplicationImageDo applicationImage);
}
