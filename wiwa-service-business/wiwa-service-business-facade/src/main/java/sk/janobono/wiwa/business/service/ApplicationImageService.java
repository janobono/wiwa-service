package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.ApplicationImageData;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;

public interface ApplicationImageService {

    Page<ApplicationImageInfoData> getApplicationImages(final Pageable pageable);

    ApplicationImageData getApplicationImage(final String fileName);

    ApplicationImageData getBoardImage(final Long boardId, final String fileName);

    ApplicationImageData getEdgeImage(final Long edgeId, final String fileName);

    ApplicationImageInfoData setApplicationImage(final MultipartFile multipartFile);

    void deleteApplicationImage(final String fileName);

    ApplicationImageData getLogo();

    ApplicationImageInfoData setLogo(final MultipartFile multipartFile);
}
