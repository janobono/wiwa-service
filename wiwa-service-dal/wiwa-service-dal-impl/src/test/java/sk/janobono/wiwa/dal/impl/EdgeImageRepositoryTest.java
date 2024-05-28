package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.janobono.wiwa.dal.repository.EdgeRepository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public EdgeImageRepository edgeImageRepository;

    @Autowired
    public EdgeRepository edgeRepository;

    @Test
    void fullTest() {
        final EdgeDo edge = edgeRepository.save(EdgeDo.builder()
                .code("code")
                .name("name")
                .weight(BigDecimal.ZERO)
                .width(BigDecimal.ZERO)
                .thickness(BigDecimal.ZERO)
                .price(BigDecimal.ZERO)
                .build());

        EdgeImageDo edgeImage = edgeImageRepository.save(EdgeImageDo.builder()
                .edgeId(edge.getId())
                .fileType("fileType")
                .data("data".getBytes(StandardCharsets.UTF_8))
                .build());

        edgeImage = edgeImageRepository.save(EdgeImageDo.builder()
                .edgeId(edgeImage.getEdgeId())
                .fileType("fileType")
                .data("data".getBytes(StandardCharsets.UTF_8))
                .build());

        Optional<EdgeImageDo> saved = edgeImageRepository.findByEdgeId(edge.getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get()).usingRecursiveComparison().isEqualTo(edgeImage);

        edgeImageRepository.deleteByEdgeId(edge.getId());

        saved = edgeImageRepository.findByEdgeId(edge.getId());
        assertThat(saved.isEmpty()).isTrue();
    }
}
