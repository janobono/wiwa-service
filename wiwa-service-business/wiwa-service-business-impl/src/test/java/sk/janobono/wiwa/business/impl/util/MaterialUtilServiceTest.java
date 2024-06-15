package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.model.CategoryData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialUtilServiceTest {

    private MaterialUtilService materialUtilService;

    @BeforeEach
    void setUp() {
        final CodeListItemRepository codeListItemRepository = Mockito.mock(CodeListItemRepository.class);
        final BoardCodeListItemRepository boardCodeListItemRepository = Mockito.mock(BoardCodeListItemRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(codeListItemRepository);
        testRepositories.mock(boardCodeListItemRepository);

        final CodeListItemDo codeListItem = codeListItemRepository.save(CodeListItemDo.builder().codeListId(1L).code("code").value("value").build());
        boardCodeListItemRepository.saveAll(1L, List.of(codeListItem.getId()));

        materialUtilService = new MaterialUtilService(boardCodeListItemRepository);
    }

    @Test
    void getMaterialNames_whenValidData_thenTheseResults() {
        Map<Long, String> data = materialUtilService.getMaterialNames(null, null, null);
        assertThat(data).isEmpty();
        data = materialUtilService.getMaterialNames(List.of(), null, null);
        assertThat(data).isEmpty();
        data = materialUtilService.getMaterialNames(List.of(), CategoryData.builder().build(), null);
        assertThat(data).isEmpty();
        data = materialUtilService.getMaterialNames(List.of(OrderBoardData.builder().id(1L).build()),
                CategoryData.builder().id(2L).build(), null);
        assertThat(data).hasSize(1);
        assertThat(data).containsKey(1L);
        assertThat(data.get(1L)).isEqualTo("");
        data = materialUtilService.getMaterialNames(List.of(OrderBoardData.builder().id(1L).build()),
                CategoryData.builder().id(2L).build(), "not found");
        assertThat(data).hasSize(1);
        assertThat(data).containsKey(1L);
        assertThat(data.get(1L)).isEqualTo("not found");
        data = materialUtilService.getMaterialNames(List.of(OrderBoardData.builder().id(1L).build()),
                CategoryData.builder().id(1L).build(), "not found");
        assertThat(data).hasSize(1);
        assertThat(data).containsKey(1L);
        assertThat(data.get(1L)).isEqualTo("value");
    }

    @Test
    void findBoard_whenValidData_thenTheseResults() {
        Optional<OrderBoardData> data = materialUtilService.findBoard(null, -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findBoard(List.of(), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findBoard(List.of(OrderBoardData.builder().build()), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findBoard(List.of(OrderBoardData.builder().id(1L).build()), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findBoard(List.of(OrderBoardData.builder().id(1L).build()), 1L);
        assertThat(data.isPresent()).isTrue();
    }

    @Test
    void findEdge_whenValidData_thenTheseResults() {
        Optional<OrderEdgeData> data = materialUtilService.findEdge(null, -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findEdge(List.of(), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findEdge(List.of(OrderEdgeData.builder().build()), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findEdge(List.of(OrderEdgeData.builder().id(1L).build()), -1L);
        assertThat(data.isEmpty()).isTrue();
        data = materialUtilService.findEdge(List.of(OrderEdgeData.builder().id(1L).build()), 1L);
        assertThat(data.isPresent()).isTrue();
    }

    @Test
    void getDecor_whenValidData_thenTheseResults() {
        assertThat(materialUtilService.getDecor(null, List.of(), -1L, null)).isEqualTo("");
        assertThat(materialUtilService.getDecor("", List.of(), -1L, null)).isEqualTo("");
        assertThat(materialUtilService.getDecor("{0} {1} {2}", List.of(), -1L, null)).isEqualTo("null null null");
        assertThat(materialUtilService.getDecor("{0} {1} {2}", List.of(OrderBoardData.builder().build()), -1L, null)).isEqualTo("null null null");
        assertThat(materialUtilService.getDecor("{0} {1} {2}", List.of(OrderBoardData.builder().build()), 1L, "not found"))
                .isEqualTo("not found not found not found");
        assertThat(materialUtilService.getDecor("{0} {1} {2}",
                List.of(OrderBoardData.builder()
                        .id(1L)
                        .build()),
                1L, "not found"))
                .isEqualTo("not found not found not found");
        assertThat(materialUtilService.getDecor("{0} {1} {2}",
                List.of(OrderBoardData.builder()
                        .id(1L)
                        .boardCode("boardCode")
                        .structureCode("structureCode")
                        .name("name")
                        .build()),
                1L, "not found"))
                .isEqualTo("boardCode structureCode name");
    }

    @Test
    void getEdge_whenValidData_thenTheseResults() {
        assertThat(materialUtilService.getEdge(null, null, null, null)).isEqualTo("");
        assertThat(materialUtilService.getEdge(null, null, -1L, null)).isEqualTo("");
        assertThat(materialUtilService.getEdge("", List.of(), -1L, null)).isEqualTo("");
        assertThat(materialUtilService.getEdge("{0}{1}{2}", List.of(), -1L, null)).isEqualTo("null00");
        assertThat(materialUtilService.getEdge("{0}{1}{2}", List.of(OrderEdgeData.builder().build()), -1L, null)).isEqualTo("null00");
        assertThat(materialUtilService.getEdge("code {0} width {1} thickness {2,number,###0.0}", List.of(), -1L, "notFound"))
                .isEqualTo("code notFound width 0 thickness 0.0");
        assertThat(materialUtilService.getEdge("code {0} width {1} thickness {2,number,###0.0}",
                List.of(OrderEdgeData.builder()
                        .id(1L)
                        .build()),
                1L, "notFound"))
                .isEqualTo("code notFound width 0 thickness 0.0");
        assertThat(materialUtilService.getEdge("code {0} width {1} thickness {2,number,###0.0}",
                List.of(OrderEdgeData.builder()
                        .id(1L)
                        .code("test")
                        .width(BigDecimal.ONE)
                        .thickness(BigDecimal.valueOf(1.234))
                        .build()),
                1L,
                "notFound"))
                .isEqualTo("code test width 1 thickness 1.2");
    }
}
