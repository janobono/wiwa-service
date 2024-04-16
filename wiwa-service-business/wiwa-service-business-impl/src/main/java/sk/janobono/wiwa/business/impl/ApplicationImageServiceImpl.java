package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.impl.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageData;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ApplicationImageServiceImpl implements ApplicationImageService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ApplicationImageRepository applicationImageRepository;
    private final BoardImageRepository boardImageRepository;
    private final EdgeImageRepository edgeImageRepository;

    @Override
    public Page<ApplicationImageInfoData> getApplicationImages(final Pageable pageable) {
        return applicationImageRepository.findAll(pageable)
                .map(applicationImageDataMapper::mapToData);
    }

    @Override
    public ApplicationImageData getApplicationImage(final String fileName) {
        return applicationImageRepository.findById(scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    @Override
    public ApplicationImageData getBoardImage(final Long boardId, final String fileName) {
        return boardImageRepository.findByBoardIdAndFileName(boardId, scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    @Override
    public ApplicationImageData getEdgeImage(final Long edgeId, final String fileName) {
        return edgeImageRepository.findByEdgeIdAndFileName(edgeId, scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    @Override
    public ApplicationImageInfoData setApplicationImage(final MultipartFile multipartFile) {
        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final ApplicationImageDo applicationImageDo = ApplicationImageDo.builder()
                .fileName(fileName)
                .fileType(fileType)
                .thumbnail(imageUtil.scaleImage(
                        fileType,
                        imageUtil.getFileData(multipartFile),
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ))
                .data(imageUtil.scaleImage(
                        fileType,
                        imageUtil.getFileData(multipartFile),
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ))
                .build();

        return applicationImageDataMapper.mapToInfoData(applicationImageRepository.save(applicationImageDo));
    }

    @Override
    public void deleteApplicationImage(final String fileName) {
        applicationImageRepository.deleteById(fileName);
    }

    @Override
    public ApplicationImageData getLogo() {
        return getApplicationImage("logo.png");
    }

    @Override
    public ApplicationImageInfoData setLogo(final MultipartFile multipartFile) {
        final String fileName = "logo.png";
        final String fileType = multipartFile.getContentType() != null ? multipartFile.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (!fileType.equals(MediaType.IMAGE_PNG_VALUE)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported logo file type {0}", fileType);
        }

        final byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final ApplicationImageDo applicationImageDo = ApplicationImageDo.builder()
                .fileName(fileName)
                .fileType(fileType)
                .thumbnail(imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxThumbnailResolution(), commonConfigProperties.maxThumbnailResolution())
                )
                .data(imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxImageResolution(), commonConfigProperties.maxImageResolution()))
                .build();

        return applicationImageDataMapper.mapToInfoData(applicationImageRepository.save(applicationImageDo));
    }
}
