package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public ApplicationImageRepository applicationImageRepository;

    @Test
    void fullTest() {
        final List<ApplicationImageDo> images = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            images.add(applicationImageRepository.save(ApplicationImageDo.builder()
                    .fileName("file%d.png".formatted(i))
                    .fileType("image/png")
                    .thumbnail("thumbnail".getBytes(StandardCharsets.UTF_8))
                    .data("data".getBytes(StandardCharsets.UTF_8))
                    .build()));
        }

        for (final ApplicationImageDo image : images) {
            final Optional<ApplicationImageDo> saved = applicationImageRepository.findById(image.getFileName());
            assertThat(saved.isPresent()).isTrue();
            assertThat(image).usingRecursiveComparison().isEqualTo(saved.get());
        }

        Page<ApplicationImageInfoDo> searchResult = applicationImageRepository.findAll(Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(10);
        assertThat(searchResult.getContent().size()).isEqualTo(10);
        assertThat(searchResult.getContent().getFirst().fileName()).isEqualTo(images.getFirst().getFileName());

        searchResult = applicationImageRepository.findAll(PageRequest.of(0, 3, Sort.Direction.ASC, "fileName"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(4);
        assertThat(searchResult.getSize()).isEqualTo(3);
        assertThat(searchResult.getContent().size()).isEqualTo(3);
        assertThat(searchResult.getContent().getFirst().fileName()).isEqualTo(images.getFirst().getFileName());

        searchResult = applicationImageRepository.findAll(PageRequest.of(0, 3, Sort.Direction.DESC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(10);
        assertThat(searchResult.getTotalPages()).isEqualTo(4);
        assertThat(searchResult.getSize()).isEqualTo(3);
        assertThat(searchResult.getContent().size()).isEqualTo(3);
        assertThat(searchResult.getContent().getFirst().fileName()).isEqualTo(images.getLast().getFileName());

        for (final ApplicationImageDo image : images) {
            applicationImageRepository.deleteById(image.getFileName());
        }

        searchResult = applicationImageRepository.findAll(Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(0);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(0);
        assertThat(searchResult.getContent().size()).isEqualTo(0);
    }
}
