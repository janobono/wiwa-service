package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;

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
}
