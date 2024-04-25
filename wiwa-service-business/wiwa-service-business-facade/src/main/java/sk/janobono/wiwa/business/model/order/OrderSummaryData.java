package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class OrderSummaryData {
    private Map<Long, OrderBoardData> boards;
    private Map<Long, OrderEdgeData> edges;
}
