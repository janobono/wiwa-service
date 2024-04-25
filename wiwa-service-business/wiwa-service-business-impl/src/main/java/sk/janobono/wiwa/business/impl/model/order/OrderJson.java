package sk.janobono.wiwa.business.impl.model.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;

@Builder
@Getter
@Setter
public class OrderJson {

    public OrderSummaryData getOrderSummaryData() {
        return OrderSummaryData.builder()
                // TODO

                .build();
    }
}
