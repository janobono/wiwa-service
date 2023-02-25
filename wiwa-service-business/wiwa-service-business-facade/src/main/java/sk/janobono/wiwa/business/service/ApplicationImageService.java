package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.common.model.ResourceEntitySo;

public interface ApplicationImageService {

    Page<ApplicationImageSo> getApplicationImages(Pageable pageable);

    ResourceEntitySo getApplicationImage(String fileName);

    ApplicationImageSo setApplicationImage(MultipartFile multipartFile);
}
