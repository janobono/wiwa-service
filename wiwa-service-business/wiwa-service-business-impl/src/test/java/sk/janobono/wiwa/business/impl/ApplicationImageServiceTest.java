package sk.janobono.wiwa.business.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.TestConfigProperties;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.impl.mapper.ApplicationImageDataMapperImpl;
import sk.janobono.wiwa.business.model.application.ApplicationImageData;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationImageServiceTest {

    private ApplicationImageService applicationImageService;

    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws Exception {
        final CommonConfigProperties commonConfigProperties = Mockito.mock(CommonConfigProperties.class);
        new TestConfigProperties().mock(commonConfigProperties);

        final ApplicationImageRepository applicationImageRepository = Mockito.mock(ApplicationImageRepository.class);
        final BoardImageRepository boardImageRepository = Mockito.mock(BoardImageRepository.class);
        final EdgeImageRepository edgeImageRepository = Mockito.mock(EdgeImageRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationImageRepository);
        testRepositories.mock(boardImageRepository);
        testRepositories.mock(edgeImageRepository);

        final byte[] imageBytes = Files.readAllBytes(
                Paths.get(Objects.requireNonNull(getClass().getResource("/linux.png")).toURI()));
        multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("linux.png");
        Mockito.when(multipartFile.getContentType()).thenReturn(MediaType.IMAGE_PNG_VALUE);
        Mockito.when(multipartFile.getBytes()).thenReturn(imageBytes);

        applicationImageService = new ApplicationImageServiceImpl(
                commonConfigProperties,
                new ImageUtil(),
                new ScDf(),
                new ApplicationImageDataMapperImpl(),
                applicationImageRepository,
                boardImageRepository,
                edgeImageRepository
        );
    }

    @Test
    void applicationImages_whenValidData_thenTheseResults() {
        Page<ApplicationImageInfoData> searchResult = applicationImageService.getApplicationImages(Pageable.unpaged());
        assertThat(searchResult.getContent()).hasSize(0);

        final ApplicationImageInfoData applicationImageInfoData = applicationImageService.setApplicationImage(multipartFile);
        assertThat(applicationImageInfoData.fileName()).isEqualTo("linux.png");
        assertThat(applicationImageInfoData.fileType()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
        assertThat(applicationImageInfoData.thumbnail()).isNotNull();

        final ApplicationImageData applicationImageData = applicationImageService.getApplicationImage("linux.png");
        assertThat(applicationImageInfoData.fileName()).isEqualTo(applicationImageData.fileName());
        assertThat(applicationImageInfoData.fileType()).isEqualTo(applicationImageData.fileType());
        assertThat(applicationImageInfoData.thumbnail()).isEqualTo(applicationImageData.thumbnail());
        assertThat(applicationImageData.data()).isNotNull();

        searchResult = applicationImageService.getApplicationImages(Pageable.unpaged());
        assertThat(searchResult.getContent()).hasSize(1);
        assertThat(searchResult.getContent().getFirst()).usingRecursiveComparison().isEqualTo(applicationImageInfoData);

        applicationImageService.deleteApplicationImage("linux.png");
        searchResult = applicationImageService.getApplicationImages(Pageable.unpaged());
        assertThat(searchResult.getContent()).hasSize(0);
    }

    @Test
    void getBoardImage_whenValidData_thenTheseResults() {
        final ApplicationImageData applicationImageData = applicationImageService.getBoardImage(1L);
        assertThat(applicationImageData.fileName()).isEqualTo("board1.png");
        assertThat(applicationImageData.fileType()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
        assertThat(applicationImageData.thumbnail()).isNotNull();
        assertThat(applicationImageData.data()).isNotNull();
    }

    @Test
    void getEdgeImage_whenValidData_thenTheseResults() {
        final ApplicationImageData applicationImageData = applicationImageService.getEdgeImage(1L);
        assertThat(applicationImageData.fileName()).isEqualTo("edge1.png");
        assertThat(applicationImageData.fileType()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
        assertThat(applicationImageData.thumbnail()).isNotNull();
        assertThat(applicationImageData.data()).isNotNull();
    }

    @Test
    void logo_whenValidData_thenTheseResults() {
        final ApplicationImageInfoData logoInfo = applicationImageService.setLogo(multipartFile);
        final ApplicationImageData logo = applicationImageService.getLogo();
        assertThat(logoInfo.fileName()).isEqualTo(logo.fileName());
        assertThat(logoInfo.fileType()).isEqualTo(logo.fileType());
        assertThat(logoInfo.thumbnail()).isEqualTo(logo.thumbnail());
        assertThat(logo.data()).isNotNull();
    }
}
