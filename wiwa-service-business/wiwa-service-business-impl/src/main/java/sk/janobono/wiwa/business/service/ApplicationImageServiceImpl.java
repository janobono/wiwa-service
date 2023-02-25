package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.mapper.ApplicationImageMapper;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.common.component.ImageUtil;
import sk.janobono.wiwa.common.component.LocalStorage;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.common.exception.WiwaException;
import sk.janobono.wiwa.common.model.ResourceEntitySo;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationImageServiceImpl implements ApplicationImageService {

    private final CommonConfigProperties commonConfigProperties;
    private final ImageUtil imageUtil;
    private final LocalStorage localStorage;
    private final ApplicationImageMapper applicationImageMapper;
    private final ApplicationImageRepository applicationImageRepository;

    public Page<ApplicationImageSo> getApplicationImages(Pageable pageable) {
        log.debug("getApplicationImages({})", pageable);
        Page<ApplicationImageSo> result = applicationImageRepository.getApplicationImages(pageable)
                .map(applicationImageMapper::map);
        log.debug("getApplicationImages({})={}", pageable, result);
        return result;
    }

    public ResourceEntitySo getApplicationImage(String fileName) {
        log.debug("getApplicationImage({})", fileName);
        if (applicationImageRepository.exists(stripAndLowerCase(fileName))) {
            ApplicationImageDo applicationImageDo = applicationImageRepository
                    .getApplicationImage(stripAndLowerCase(fileName)).orElseThrow();
            return new ResourceEntitySo(
                    applicationImageDo.fileName(),
                    applicationImageDo.fileType(),
                    localStorage.getDataResource(applicationImageDo.data())
            );
        } else {
            return new ResourceEntitySo(
                    fileName,
                    MediaType.IMAGE_PNG_VALUE,
                    localStorage.getDataResource(imageUtil.generateMessageImage(null))
            );
        }
    }

    public ApplicationImageSo setApplicationImage(MultipartFile multipartFile) {
        log.debug("setApplicationImage({})", multipartFile.getOriginalFilename());
        String fileName = stripAndLowerCase(multipartFile.getOriginalFilename());
        String fileType = multipartFile.getContentType() != null
                ? multipartFile.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (!(fileType.equals(MediaType.IMAGE_GIF_VALUE)
                || fileType.equals(MediaType.IMAGE_JPEG_VALUE)
                || fileType.equals(MediaType.IMAGE_PNG_VALUE))
        ) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ApplicationImageDo applicationImageDo = new ApplicationImageDo(
                fileName,
                fileType,
                imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ),
                imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxImageResolution(),
                        commonConfigProperties.maxImageResolution()
                )
        );

        ApplicationImageSo result;
        if (applicationImageRepository.exists(fileName)) {
            result = applicationImageMapper.map(applicationImageRepository.setApplicationImage(applicationImageDo));
        } else {
            result = applicationImageMapper.map(applicationImageRepository.addApplicationImage(applicationImageDo));
        }
        log.debug("setApplicationImage({})={}", multipartFile.getOriginalFilename(), result);
        return result;
    }

    private String stripAndLowerCase(String s) {
        if (StringUtils.hasLength(s)) {
            return s.strip().toLowerCase();
        }
        return s;
    }
}
