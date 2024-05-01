package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.application.PriceForGluingLayerData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.model.order.summary.*;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SummaryUtil {

    private final BoardAreaCalculationUtil boardAreaCalculationUtil;
    private final PriceUtil priceUtil;

    public OrderItemSummaryData calculateItemSummary(final PartData part,
                                                     final int quantity,
                                                     final Map<Long, BigDecimal> boardThickness,
                                                     final ManufacturePropertiesData manufactureProperties) {
        final OrderItemPartSummaryData partSummary = countPartSummary(part, boardThickness, manufactureProperties);

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

    private OrderItemPartSummaryData countPartSummary(final PartData part,
                                                      final Map<Long, BigDecimal> boardThickness,
                                                      final ManufacturePropertiesData manufactureProperties) {
        return OrderItemPartSummaryData.builder()
                .boardSummary(countBoardSummary(part, manufactureProperties))
                .edgeSummary(countEdgedSummary(part, manufactureProperties))
                .gluedArea(countGluedArea(part, manufactureProperties))
                .cutSummary(countCutSummary(part, boardThickness, manufactureProperties))
                .build();
    }

    private List<OrderBoardSummaryData> countBoardSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, BigDecimal> boardAreaMap = boardAreaCalculationUtil.calculateArea(part, manufactureProperties)
                .entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(part.boards().get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));

        return boardAreaMap.entrySet().stream()
                .map(entry -> new OrderBoardSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<OrderEdgeSummaryData> countEdgedSummary(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, BigDecimal> edgeLengthMap = new HashMap<>();
        final Map<Long, BigDecimal> edgeGlueLengthMap = new HashMap<>();
        final Map<BoardPosition, DimensionsData> dimensions = part.dimensions();

        for (final Map.Entry<EdgePosition, Long> edgeEntry : part.edges().entrySet()) {
            final Long edgeId = edgeEntry.getValue();
            BigDecimal edgeLength = edgeLengthMap.getOrDefault(edgeId, BigDecimal.ZERO);
            BigDecimal edgeGlueLength = edgeGlueLengthMap.getOrDefault(edgeId, BigDecimal.ZERO);

            switch (edgeEntry.getKey()) {
                case A1, A2 -> {
                    final BigDecimal length = dimensions.get(BoardPosition.TOP).x();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
                case B1, B2 -> {
                    final BigDecimal length = dimensions.get(BoardPosition.TOP).y();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
                case A1I -> {
                    final BigDecimal length = dimensions.get(BoardPosition.A1).x();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
                case A2I -> {
                    final BigDecimal length = dimensions.get(BoardPosition.A2).x();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
                case B1I -> {
                    final BigDecimal length = dimensions.get(BoardPosition.B1).y();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
                case B2I -> {
                    final BigDecimal length = dimensions.get(BoardPosition.B2).y();
                    edgeLength = edgeLength.add(millimeterToMeter(length.add(manufactureProperties.edgeLengthAppend())));
                    edgeGlueLength = edgeGlueLength.add(millimeterToMeter(length));
                }
            }

            edgeLengthMap.put(edgeId, edgeLength);
            edgeGlueLengthMap.put(edgeId, edgeGlueLength);
        }

        return edgeLengthMap.keySet().stream()
                .map(edgeId -> new OrderEdgeSummaryData(
                        edgeId,
                        edgeLengthMap.get(edgeId),
                        edgeGlueLengthMap.get(edgeId)
                ))
                .toList();
    }

    private BigDecimal countGluedArea(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        return switch (part) {
            case final PartDuplicatedBasicData duplicatedBasic -> countArea(
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

    private List<OrderCutSummaryData> countCutSummary(final PartData part,
                                                      final Map<Long, BigDecimal> boardThickness,
                                                      final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        switch (part) {
            case final PartDuplicatedBasicData partDuplicatedBasic -> {
                final BigDecimal topThickness = boardThickness.get(partDuplicatedBasic.boardId());
                final BigDecimal topCutLength = cutMap.getOrDefault(topThickness, BigDecimal.ZERO);
                cutMap.put(topThickness, topCutLength.add(
                        countPerimeter(
                                partDuplicatedBasic.dimensionsTOP()
                                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                        )
                ));

                final BigDecimal bottomThickness = boardThickness.get(partDuplicatedBasic.boardIdBottom());
                final BigDecimal bottomCutLength = cutMap.getOrDefault(bottomThickness, BigDecimal.ZERO);
                cutMap.put(bottomThickness, bottomCutLength.add(
                        countPerimeter(
                                partDuplicatedBasic.dimensionsTOP()
                                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                        )
                ));

                final BigDecimal finalThickness = topThickness.add(bottomThickness);
                final BigDecimal finalCutLength = cutMap.getOrDefault(finalThickness, BigDecimal.ZERO);
                cutMap.put(finalThickness, finalCutLength.add(countPerimeter(partDuplicatedBasic.dimensionsTOP())));
            }
            case final PartDuplicatedFrameData partDuplicatedFrame -> {
                final Map<BoardPosition, Long> frameBoards = partDuplicatedFrame.boards().entrySet().stream()
                        .filter(entry -> switch (entry.getKey()) {
                            case A1, A2, B1, B2 -> true;
                            default -> false;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                cutBoardsCutSummary(part, boardThickness, manufactureProperties, cutMap);

                // Final cut
                final BigDecimal topThickness = boardThickness.get(partDuplicatedFrame.boardId());
                final BigDecimal frameThickness = frameBoards.values().stream()
                        .findFirst()
                        .map(boardThickness::get)
                        .orElse(BigDecimal.ZERO);
                final BigDecimal finalThickness = topThickness.add(frameThickness);

                BigDecimal topCutLength = cutMap.getOrDefault(topThickness, BigDecimal.ZERO);
                BigDecimal finalCutLength = cutMap.getOrDefault(finalThickness, BigDecimal.ZERO);

                // A1, A2
                for (final BoardPosition boardPosition : Set.of(BoardPosition.A1, BoardPosition.A2)) {
                    final BigDecimal topThicknessLength = countTopLength(
                            partDuplicatedFrame,
                            boardPosition,
                            partDuplicatedFrame.dimensionsTOP().x(),
                            Set.of(BoardPosition.B1, BoardPosition.B2),
                            DimensionsData::y);

                    topCutLength = addLength(topThicknessLength, topCutLength);

                    final BigDecimal finalThicknessLength = partDuplicatedFrame.dimensionsTOP().x()
                            .subtract(topThicknessLength);

                    finalCutLength = addLength(finalThicknessLength, finalCutLength);
                }
                // B1, B2
                for (final BoardPosition boardPosition : Set.of(BoardPosition.B1, BoardPosition.B2)) {
                    final BigDecimal topThicknessLength = countTopLength(
                            partDuplicatedFrame,
                            boardPosition,
                            partDuplicatedFrame.dimensionsTOP().y(),
                            Set.of(BoardPosition.A1, BoardPosition.A2),
                            DimensionsData::x);

                    topCutLength = addLength(topThicknessLength, topCutLength);

                    final BigDecimal finalThicknessLength = partDuplicatedFrame.dimensionsTOP().x()
                            .subtract(topThicknessLength);

                    finalCutLength = addLength(finalThicknessLength, finalCutLength);
                }

                cutMap.put(topThickness, topCutLength);
                cutMap.put(finalThickness, finalCutLength);
            }
            default -> cutBoardsCutSummary(part, boardThickness, manufactureProperties, cutMap);
        }

        return cutMap.entrySet().stream()
                .map(entry -> new OrderCutSummaryData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void cutBoardsCutSummary(final PartData part,
                                     final Map<Long, BigDecimal> boardThickness,
                                     final ManufacturePropertiesData manufactureProperties,
                                     final Map<BigDecimal, BigDecimal> cutMap) {
        for (final Map.Entry<BoardPosition, Long> boardEntry : part.boards().entrySet()) {
            final BigDecimal thickness = boardThickness.get(boardEntry.getValue());
            final BigDecimal cutLength = cutMap.getOrDefault(thickness, BigDecimal.ZERO);
            cutMap.put(thickness, cutLength.add(countPerimeter(part.dimensions().get(boardEntry.getKey()))));
        }
    }

    private BigDecimal countTopLength(final PartData part,
                                      final BoardPosition bottomPosition,
                                      final BigDecimal length,
                                      final Set<BoardPosition> connectedBottomPositions,
                                      final Function<DimensionsData, BigDecimal> mapConnectedDimensions) {
        if (part.boards().containsKey(bottomPosition)) {
            return BigDecimal.ZERO;
        }

        final BigDecimal connectedLength = connectedBottomPositions.stream()
                .filter(boardPosition -> part.boards().containsKey(boardPosition))
                .map(boardPosition -> part.dimensions().get(boardPosition))
                .map(mapConnectedDimensions)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return length.subtract(connectedLength);
    }

    private BigDecimal addLength(BigDecimal augend, BigDecimal length) {
        if (augend.compareTo(BigDecimal.ZERO) > 0) {
            length = length.add(millimeterToMeter(augend));
        }
        return length;
    }

    private BigDecimal millimeterToMeter(final BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }

    private BigDecimal countArea(final DimensionsData dimensions) {
        return millimeterToMeter(dimensions.x())
                .multiply(millimeterToMeter(dimensions.y()))
                .setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal countPerimeter(final DimensionsData dimensions) {
        return millimeterToMeter(dimensions.x())
                .add(millimeterToMeter(dimensions.y()))
                .multiply(BigDecimal.TWO)
                .setScale(3, RoundingMode.HALF_UP);
    }
}
