package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.model.OrderMaterialIdDo;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MaterialUtil {

    private static final String BOARD = "BOARD";
    private static final String EDGE = "EDGE";

    private final DataUtil dataUtil;

    public List<OrderBoardData> toBoards(final List<OrderMaterialDo> materials) {
        return materials.stream()
                .filter(m -> BOARD.equals(m.getCode()))
                .map(m -> dataUtil.parseValue(m.getData(), OrderBoardData.class))
                .toList();
    }

    public List<OrderEdgeData> toEdges(final List<OrderMaterialDo> materials) {
        return materials.stream()
                .filter(m -> EDGE.equals(m.getCode()))
                .map(m -> dataUtil.parseValue(m.getData(), OrderEdgeData.class))
                .toList();
    }

    public OrderMaterialIdDo toBoardId(final long orderId, final long boardId) {
        return OrderMaterialIdDo.builder()
                .orderId(orderId)
                .materialId(boardId)
                .code(BOARD)
                .build();
    }

    public OrderMaterialIdDo toEdgeId(final long orderId, final long edgeId) {
        return OrderMaterialIdDo.builder()
                .orderId(orderId)
                .materialId(edgeId)
                .code(EDGE)
                .build();
    }

    public OrderMaterialDo toMaterial(final long orderId, final OrderBoardData orderBoard) {
        return OrderMaterialDo.builder()
                .orderId(orderId)
                .materialId(orderBoard.id())
                .code(BOARD)
                .data(dataUtil.serializeValue(orderBoard))
                .build();
    }

    public OrderMaterialDo toMaterial(final long orderId, final OrderEdgeData orderEdge) {
        return OrderMaterialDo.builder()
                .orderId(orderId)
                .materialId(orderEdge.id())
                .code(EDGE)
                .data(dataUtil.serializeValue(orderEdge))
                .build();
    }
}
