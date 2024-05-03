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

    private static final String CODE_SEPARATOR = "::";
    private static final String PART = "PART";
    private static final String TOTAL = "TOTAL";
    private static final String BOARD = "BOARD";
    private static final String EDGE = "EDGE";
    private static final String CONSUMPTION = "CONSUMPTION";
    private static final String GLUE = "GLUE";
    private static final String CUT = "CUT";

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
                        .map(bs -> new OrderItemBoardSummaryData(
                                bs.id(),
                                bs.area().multiply(BigDecimal.valueOf(quantity))
                        ))
                        .toList()
                )
                .edgeSummary(partSummary.edgeSummary().stream()
                        .map(es -> new OrderItemEdgeSummaryData(
                                es.id(),
                                es.length().multiply(BigDecimal.valueOf(quantity)),
                                es.glueLength().multiply(BigDecimal.valueOf(quantity))
                        ))
                        .toList())
                .gluedArea(partSummary.gluedArea().multiply(BigDecimal.valueOf(quantity)))
                .cutSummary(partSummary.cutSummary().stream()
                        .map(cs -> new OrderItemCutSummaryData(cs.thickness(), cs.amount().multiply(BigDecimal.valueOf(quantity))))
                        .toList())
                .build();
        return new OrderItemSummaryData(partSummary, totalSummary);
    }

    public List<OrderItemSummaryDo> toOrderItemSummaries(final Long itemId, final OrderItemSummaryData orderItemSummary) {
        final List<OrderItemSummaryDo> orderItemSummaries = new ArrayList<>();

        orderItemSummaries.addAll(mapBoardSummaries(itemId, PART, orderItemSummary.partSummary().boardSummary()));
        orderItemSummaries.addAll(mapEdgeSummaries(itemId, PART, orderItemSummary.partSummary().edgeSummary()));
        orderItemSummaries.add(OrderItemSummaryDo.builder()
                .orderItemId(itemId)
                .code(createCode(PART, GLUE))
                .amount(orderItemSummary.partSummary().gluedArea())
                .build());
        orderItemSummaries.addAll(mapCutSummaries(itemId, PART, orderItemSummary.partSummary().cutSummary()));

        orderItemSummaries.addAll(mapBoardSummaries(itemId, TOTAL, orderItemSummary.totalSummary().boardSummary()));
        orderItemSummaries.addAll(mapEdgeSummaries(itemId, TOTAL, orderItemSummary.totalSummary().edgeSummary()));
        orderItemSummaries.add(OrderItemSummaryDo.builder()
                .orderItemId(itemId)
                .code(createCode(TOTAL, GLUE))
                .amount(orderItemSummary.totalSummary().gluedArea())
                .build());
        orderItemSummaries.addAll(mapCutSummaries(itemId, TOTAL, orderItemSummary.totalSummary().cutSummary()));

        return orderItemSummaries;
    }

    public OrderItemSummaryData toOrderItemSummary(final List<OrderItemSummaryDo> orderItemSummaries) {
        return OrderItemSummaryData.builder()
                .partSummary(toSummary(PART, orderItemSummaries))
                .totalSummary(toSummary(TOTAL, orderItemSummaries))
                .build();
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

    private List<OrderItemBoardSummaryData> calculateBoardSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, BigDecimal> boardAreaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties)
                .entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(part.boards().get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));

        return boardAreaMap.entrySet().stream()
                .map(entry -> new OrderItemBoardSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<OrderItemEdgeSummaryData> calculateEdgeSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, EdgeLengthData> edgeLengthMap = edgeLengthCalculationUtil.calculateEdgeLength(part, manufactureProperties);

        return edgeLengthMap.entrySet().stream()
                .map(entry -> new OrderItemEdgeSummaryData(
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

    private List<OrderItemCutSummaryData> calculateCutSummary(final PartData part,
                                                              final Map<Long, BigDecimal> boardThickness,
                                                              final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutLenghtMap = cutLengthCalculationUtil
                .calculateBoardCutLength(part, boardThickness, manufactureProperties);
        return cutLenghtMap.entrySet().stream()
                .map(entry -> new OrderItemCutSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<OrderItemSummaryDo> mapBoardSummaries(final Long itemId, final String prefix, final List<OrderItemBoardSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new ArrayList<>();
        for (final OrderItemBoardSummaryData orderBoardSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, BOARD, orderBoardSummary.id().toString()))
                    .amount(orderBoardSummary.area())
                    .build());
        }
        return orderItemSummaries;
    }

    private List<OrderItemSummaryDo> mapEdgeSummaries(final Long itemId, final String prefix, final List<OrderItemEdgeSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new ArrayList<>();
        for (final OrderItemEdgeSummaryData orderEdgeSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, EDGE, CONSUMPTION, orderEdgeSummary.id().toString()))
                    .amount(orderEdgeSummary.length())
                    .build());

            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, EDGE, GLUE, orderEdgeSummary.id().toString()))
                    .amount(orderEdgeSummary.glueLength())
                    .build());
        }
        return orderItemSummaries;
    }

    private List<OrderItemSummaryDo> mapCutSummaries(final Long itemId, final String prefix, final List<OrderItemCutSummaryData> items) {
        final List<OrderItemSummaryDo> orderItemSummaries = new ArrayList<>();
        for (final OrderItemCutSummaryData orderCutSummary : items) {
            orderItemSummaries.add(OrderItemSummaryDo.builder()
                    .orderItemId(itemId)
                    .code(createCode(prefix, CUT, orderCutSummary.thickness().toPlainString()))
                    .amount(orderCutSummary.amount())
                    .build());
        }
        return orderItemSummaries;
    }

    private OrderItemPartSummaryData toSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return OrderItemPartSummaryData.builder()
                .boardSummary(toBoardSummary(prefix, items))
                .edgeSummary(toEdgeSummary(prefix, items))
                .gluedArea(toGluedArea(prefix, items))
                .cutSummary(toCutSummary(prefix, items))
                .build();
    }

    private List<OrderItemBoardSummaryData> toBoardSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, BOARD)))
                .map(item -> new OrderItemBoardSummaryData(Long.valueOf(parseCode(item.getCode())[2]), item.getAmount()))
                .toList();
    }

    private List<OrderItemEdgeSummaryData> toEdgeSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        final List<OrderItemSummaryDo> edgeSummaries = items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, EDGE, CONSUMPTION))
                        || item.getCode().startsWith(createCode(prefix, EDGE, GLUE)))
                .toList();

        return edgeSummaries.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, EDGE, CONSUMPTION)))
                .map(item -> Long.valueOf(parseCode(item.getCode())[3]))
                .map(id -> {
                    final BigDecimal length = edgeSummaries.stream()
                            .filter(item -> item.getCode().equals(createCode(prefix, EDGE, CONSUMPTION, id.toString())))
                            .map(OrderItemSummaryDo::getAmount)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);

                    final BigDecimal glueLength = edgeSummaries.stream()
                            .filter(item -> item.getCode().equals(createCode(prefix, EDGE, GLUE, id.toString())))
                            .map(OrderItemSummaryDo::getAmount)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);

                    return new OrderItemEdgeSummaryData(id, length, glueLength);
                })
                .toList();
    }

    private BigDecimal toGluedArea(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().equals(createCode(prefix, GLUE)))
                .findFirst()
                .map(OrderItemSummaryDo::getAmount)
                .orElse(BigDecimal.ZERO);
    }

    private List<OrderItemCutSummaryData> toCutSummary(final String prefix, final List<OrderItemSummaryDo> items) {
        return items.stream()
                .filter(item -> item.getCode().startsWith(createCode(prefix, CUT)))
                .map(item -> new OrderItemCutSummaryData(new BigDecimal(parseCode(item.getCode())[2]), item.getAmount()))
                .toList();
    }

    private String createCode(final String... elements) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            builder.append(elements[i]);
            if (i < elements.length - 1) {
                builder.append(CODE_SEPARATOR);
            }
        }
        return builder.toString();
    }

    private String[] parseCode(final String code) {
        return code.split(CODE_SEPARATOR);
    }
}
