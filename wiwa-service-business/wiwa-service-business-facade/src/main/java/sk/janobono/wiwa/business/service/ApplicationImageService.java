package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.application.ApplicationImageData;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;

public interface ApplicationImageService {

    Page<ApplicationImageInfoData> getApplicationImages(Pageable pageable);

    ApplicationImageData getApplicationImage(String fileName);

    ApplicationImageData getBoardImage(long boardId, String fileName);

    ApplicationImageData getEdgeImage(long edgeId, String fileName);

    ApplicationImageInfoData setApplicationImage(MultipartFile multipartFile);

    void deleteApplicationImage(String fileName);

    ApplicationImageData getLogo();

    ApplicationImageInfoData setLogo(MultipartFile multipartFile);
}
