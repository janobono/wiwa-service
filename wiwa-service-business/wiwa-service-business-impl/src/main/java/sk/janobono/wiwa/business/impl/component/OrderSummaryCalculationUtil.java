package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderSummaryCalculationUtil extends BaseCalculationUtil {

    private final PriceUtil priceUtil;

    public OrderSummaryData calculateOrderSummary(final List<OrderBoardData> boards,
                                                  final List<OrderEdgeData> edges,
                                                  final BigDecimal vatRate,
                                                  final List<PriceForCuttingData> pricesForCutting,
                                                  final BigDecimal priceForGluingLayer,
                                                  final List<PriceForGluingEdgeData> pricesForGluingEdge,
                                                  final List<OrderSummaryViewDo> orderSummaries) {



// TODO
        return OrderSummaryData.builder()
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .vatTotal(BigDecimal.ZERO)
                // TODO
                .build();
    }
}
