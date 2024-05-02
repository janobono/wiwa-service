package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.impl.model.summary.EdgeLengthData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.application.PriceForGluingLayerData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SummaryUtil extends BaseCalculationUtil {

    private final BoardAreaCalculationUtil boardAreaCalculationUtil;
    private final CutLengthCalculationUtil cutLengthCalculationUtil;
    private final EdgeLengthCalculationUtil edgeLengthCalculationUtil;
    private final PriceUtil priceUtil;

    public OrderItemSummaryData calculateItemSummary(final PartData part,
                                                     final int quantity,
                                                     final Map<Long, BigDecimal> boardThickness,
                                                     final ManufacturePropertiesData manufactureProperties) {
        final OrderItemPartSummaryData partSummary = calculatePartSummary(part, boardThickness, manufactureProperties);

        final OrderItemPartSummaryData totalSummary = OrderItemPartSummaryData.builder()
                .boardSummary(partSummary.boardSummary().stream()
                        .map(bs -> new OrderBoardSummaryData(
                                bs.id(),
                                bs.area().multiply(BigDecimal.valueOf(quantity))
                        ))
                        .toList()
                )
                .edgeSummary(partSummary.edgeSummary().stream()
                        .map(es -> new OrderEdgeSummaryData(
                                es.id(),
                                es.length().multiply(BigDecimal.valueOf(quantity)),
                                es.glueLength().multiply(BigDecimal.valueOf(quantity))
                        ))
                        .toList())
                .gluedArea(partSummary.gluedArea().multiply(BigDecimal.valueOf(quantity)))
                .cutSummary(partSummary.cutSummary().stream()
                        .map(cs -> new OrderCutSummaryData(cs.thickness(), cs.amount().multiply(BigDecimal.valueOf(quantity))))
                        .toList())
                .build();
        return new OrderItemSummaryData(partSummary, totalSummary);
    }

    public List<OrderItemSummaryDo> toOrderItemSummaries(final Long itemId, final OrderItemSummaryData orderItemSummary) {
        final List<OrderItemSummaryDo> orderItemSummaries = new ArrayList<>();

        // TODO
        return orderItemSummaries;
    }

    public OrderItemSummaryData toOrderItemSummary(final List<OrderItemSummaryDo> orderItemSummaries) {
        // TODO
        return null;
    }

    public OrderSummaryData toOrderSummary(
            Map<Long, OrderBoardData> boards,
            Map<Long, OrderEdgeData> edges,
            BigDecimal vatRate,
            List<PriceForCuttingData> pricesForCutting,
            PriceForGluingLayerData priceForGluingLayer,
            List<PriceForGluingEdgeData> pricesForGluingEdge,
            List<OrderSummaryViewDo> orderSummaries) {
        // TODO
        return null;
    }

    public OrderSummaryData createEmptySummary() {
        return OrderSummaryData.builder()
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .vatTotal(BigDecimal.ZERO)
                // TODO


                .build();
    }

    private OrderItemPartSummaryData calculatePartSummary(final PartData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        return OrderItemPartSummaryData.builder()
                .boardSummary(calculateBoardSummary(part, manufactureProperties))
                .edgeSummary(calculateEdgeSummary(part, manufactureProperties))
                .gluedArea(calculateGluedArea(part, manufactureProperties))
                .cutSummary(calculateCutSummary(part, boardThickness, manufactureProperties))
                .build();
    }

    private List<OrderBoardSummaryData> calculateBoardSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, BigDecimal> boardAreaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties)
                .entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(part.boards().get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));

        return boardAreaMap.entrySet().stream()
                .map(entry -> new OrderBoardSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<OrderEdgeSummaryData> calculateEdgeSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        return edgeLengthMap.entrySet().stream()
                .map(entry -> new OrderEdgeSummaryData(
                        entry.getKey(),
                        entry.getValue().consumption(),
                        entry.getValue().length()
                ))
                .toList();
    }

    private BigDecimal calculateGluedArea(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        return switch (part) {
            case final PartDuplicatedBasicData duplicatedBasic -> calculateArea(
                    duplicatedBasic.dimensionsTOP()
                            .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
            );
            case final PartDuplicatedFrameData duplicatedFrame ->
                    boardAreaCalculationUtil.calculateArea(duplicatedFrame, manufactureProperties)
                            .entrySet().stream()
                            .filter(entry -> switch (entry.getKey()) {
                                case A1, A2, B1, B2 -> true;
                                default -> false;
                            })
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            default -> BigDecimal.ZERO;
        };
    }

    private List<OrderCutSummaryData> calculateCutSummary(final PartData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutLenghtMap = cutLengthCalculationUtil
                .calculateBoardCutLength(part, boardThickness, manufactureProperties);
        return cutLenghtMap.entrySet().stream()
                .map(entry -> new OrderCutSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }
}
