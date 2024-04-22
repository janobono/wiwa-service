package sk.janobono.wiwa.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.ResourceEntityWebDto;
import sk.janobono.wiwa.business.model.application.ApplicationImageData;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;
import sk.janobono.wiwa.component.ImageUtil;

@RequiredArgsConstructor
@Component
public class ApplicationImageWebMapper {

    private final ImageUtil imageUtil;

    public ApplicationImageInfoWebDto mapToWebDto(final ApplicationImageInfoData applicationImageInfo) {
        return new ApplicationImageInfoWebDto(
                applicationImageInfo.fileName(),
                applicationImageInfo.fileType(),
                imageUtil.toThumbnail(applicationImageInfo.fileType(), applicationImageInfo.thumbnail())
        );
    }

    public ResourceEntityWebDto mapToWebDto(final ApplicationImageData applicationImage) {
        return new ResourceEntityWebDto(
                applicationImage.fileName(),
                applicationImage.fileType(),
                imageUtil.getDataResource(applicationImage.data())
        );
    }
}
