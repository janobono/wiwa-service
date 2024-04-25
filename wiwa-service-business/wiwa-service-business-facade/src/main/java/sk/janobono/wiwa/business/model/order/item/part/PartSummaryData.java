package sk.janobono.wiwa.business.model.order.item.part;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class PartSummaryData {
    private String name;
    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private Quantity partWeight;
    private Money partPrice;
    private Integer amount;
    private Quantity weight;
    private Money total;

    public Map<Long, OrderBoardData> getBoards() {
        if (boards == null) {
            boards = new HashMap<>();
        }
        return boards;
    }

    public Map<Long, OrderEdgeData> getEdges() {
        if (edges == null) {
            edges = new HashMap<>();
        }
        return edges;
    }
}
