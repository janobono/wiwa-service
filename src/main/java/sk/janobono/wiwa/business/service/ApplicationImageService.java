package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.LocalStorage;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.mapper.ApplicationImageMapper;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.ResourceEntity;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApplicationImageService {
    private final CommonConfigProperties commonConfigProperties;
    private final ApplicationImageMapper applicationImageMapper;
    private final ImageUtil imageUtil;
    private final LocalStorage localStorage;
    private final ScDf scDf;
    private final ApplicationImageRepository applicationImageRepository;

    public Page<ApplicationImage> getApplicationImages(final Pageable pageable) {
        return applicationImageRepository.findAll(pageable)
                .map(applicationImageMapper::map);
    }

    public ResourceEntity getApplicationImage(final String fileName) {
        return applicationImageRepository.findById(scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDo -> new ResourceEntity(
                        applicationImageDo.getFileName(),
                        applicationImageDo.getFileType(),
                        localStorage.getDataResource(applicationImageDo.getData())
                ))
                .orElse(new ResourceEntity(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        localStorage.getDataResource(imageUtil.generateMessageImage(null))
                ));
    }

    @Transactional
    public ApplicationImage setApplicationImage(final MultipartFile multipartFile) {
        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!localStorage.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final ApplicationImageDo applicationImageDo = new ApplicationImageDo();
        applicationImageDo.setFileName(fileName);
        applicationImageDo.setFileType(fileType);
        applicationImageDo.setThumbnail(imageUtil.scaleImage(
                fileType,
                localStorage.getFileData(multipartFile),
                commonConfigProperties.maxThumbnailResolution(),
                commonConfigProperties.maxThumbnailResolution()
        ));
        applicationImageDo.setData(imageUtil.scaleImage(
                fileType,
                localStorage.getFileData(multipartFile),
                commonConfigProperties.maxImageResolution(),
                commonConfigProperties.maxImageResolution()
        ));

        return applicationImageMapper.map(applicationImageRepository.save(applicationImageDo));
    }

    @Transactional
    public void deleteApplicationImage(final String fileName) {
        applicationImageRepository.deleteById(fileName);
    }
}
