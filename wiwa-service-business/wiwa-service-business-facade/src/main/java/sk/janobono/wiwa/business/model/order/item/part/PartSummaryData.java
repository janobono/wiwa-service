package sk.janobono.wiwa.business.model.order.item.part;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class PartSummaryData {
    private String name;
    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
    private BigDecimal partWeight;
    private BigDecimal partPrice;
    private Integer amount;
    private BigDecimal weight;
    private BigDecimal total;

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


//export interface SummaryBorderItem {
//    borderId: number;
//    length: number;
//    price: number;
//    stickLength: number;
//    stickPrice: number;
//}

//export interface SummaryMaterialItem {
//    materialId: number;
//    itemCount: number;
//    sawLength: number;
//    sawPrice: number;
//    doubleSawLength: number;
//    doubleSawPrice: number;
//    rawArea: number;
//    materialCount: number;
//    materialPrice: number;
//    stickArea: number;
//    stickPrice: number;
//    borderItems: SummaryBorderItem[];
//}

//export interface Summary {
//    materialItems: SummaryMaterialItem[];
//    materialItem: SummaryMaterialItem;
//    borderItems: SummaryBorderItem[];
//    borderItem: SummaryBorderItem;
//    totalPrice: number;
//}
