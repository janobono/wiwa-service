package sk.janobono.wiwa.business.impl.model.order;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.business.model.order.OrderSummaryData;

@Builder
@Data
public class OrderJson {

    public OrderSummaryData getOrderSummaryData() {
        return OrderSummaryData.builder()
                // TODO

                .build();
    }
}
