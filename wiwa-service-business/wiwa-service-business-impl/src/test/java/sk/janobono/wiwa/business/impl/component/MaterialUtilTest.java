package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialUtilTest {

    private MaterialUtil materialUtil;
    private OrderBoardData orderBoard;
    private OrderEdgeData orderEdge;
    private List<OrderMaterialDo> materials;

    @BeforeEach
    void setUp() {
        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        final DataUtil dataUtil = new DataUtil(objectMapper);
        materialUtil = new MaterialUtil(dataUtil);

        orderBoard = OrderBoardData.builder()
                .id(1L)
                .build();
        orderEdge = OrderEdgeData.builder()
                .id(2L)
                .build();

        materials = List.of(
                OrderMaterialDo.builder()
                        .orderId(1L)
                        .materialId(1L)
                        .code("BOARD")
                        .data(dataUtil.serializeValue(orderBoard))
                        .build(),
                OrderMaterialDo.builder()
                        .orderId(1L)
                        .materialId(1L)
                        .code("EDGE")
                        .data(dataUtil.serializeValue(orderEdge))
                        .build()
        );
    }

    @Test
    void toBoards_whenValidData_thenTheseResults() {
        final List<OrderBoardData> boards = materialUtil.toBoards(materials);
        assertThat(boards).hasSize(1);
        assertThat(boards.getFirst().id()).isEqualTo(1L);
    }

    @Test
    void toEdges_whenValidData_thenTheseResults() {
        final List<OrderEdgeData> edges = materialUtil.toEdges(materials);
        assertThat(edges).hasSize(1);
        assertThat(edges.getFirst().id()).isEqualTo(2L);
    }

    @Test
    void toMaterial_whenValidData_thenTheseResults() {
        OrderMaterialDo orderMaterial = materialUtil.toMaterial(1L, orderBoard);
        assertThat(orderMaterial.getOrderId()).isEqualTo(1L);
        orderMaterial = materialUtil.toMaterial(2L, orderEdge);
        assertThat(orderMaterial.getOrderId()).isEqualTo(2L);
    }
}
