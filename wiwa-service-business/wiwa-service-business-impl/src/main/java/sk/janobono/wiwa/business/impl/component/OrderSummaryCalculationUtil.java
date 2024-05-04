package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.PriceForCuttingData;
import sk.janobono.wiwa.business.model.application.PriceForGluingEdgeData;
import sk.janobono.wiwa.business.model.application.PriceForGluingLayerData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.summary.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class OrderSummaryCalculationUtil extends BaseCalculationUtil {

    private final PriceUtil priceUtil;

    public OrderSummaryData calculateOrderSummary(final List<OrderBoardData> boards,
                                                  final List<OrderEdgeData> edges,
                                                  final BigDecimal vatRate,
                                                  final List<PriceForCuttingData> pricesForCutting,
                                                  final PriceForGluingLayerData priceForGluingLayer,
                                                  final List<PriceForGluingEdgeData> pricesForGluingEdge,
                                                  final OrderItemPartSummaryData totalPartSummary) {
        final List<OrderBoardSummaryData> boardSummary = totalPartSummary.boardSummary().stream()
                .map(item -> calculateBoardSummary(boards, vatRate, item))
                .toList();

        final List<OrderEdgeSummaryData> edgeSummary = totalPartSummary.edgeSummary().stream()
                .map(item -> calculateEdgeSummary(edges, vatRate, pricesForGluingEdge, item))
                .toList();

        final OrderGlueSummaryData glueSummary = calculateGlueSummary(vatRate, priceForGluingLayer, totalPartSummary.gluedArea());

        final List<OrderCutSummaryData> cutSummary = totalPartSummary.cutSummary().stream()
                .map(item -> calculateCutSummary(vatRate, pricesForCutting, item))
                .toList();

        return OrderSummaryData.builder()
                .boardSummary(boardSummary)
                .edgeSummary(edgeSummary)
                .glueSummary(glueSummary)
                .cutSummary(cutSummary)
                .weight(
                        Stream.concat(
                                boardSummary.stream().map(OrderBoardSummaryData::weight),
                                edgeSummary.stream().map(OrderEdgeSummaryData::weight)
                        ).reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .total(
                        Stream.of(
                                        boardSummary.stream().map(OrderBoardSummaryData::price),
                                        edgeSummary.stream().map(OrderEdgeSummaryData::gluePrice),
                                        edgeSummary.stream().map(OrderEdgeSummaryData::edgePrice),
                                        Stream.of(glueSummary.price()),
                                        cutSummary.stream().map(OrderCutSummaryData::price)
                                )
                                .flatMap(s -> s)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .vatTotal(
                        Stream.of(
                                        boardSummary.stream().map(OrderBoardSummaryData::vatPrice),
                                        edgeSummary.stream().map(OrderEdgeSummaryData::glueVatPrice),
                                        edgeSummary.stream().map(OrderEdgeSummaryData::edgeVatPrice),
                                        Stream.of(glueSummary.vatPrice()),
                                        cutSummary.stream().map(OrderCutSummaryData::vatPrice)
                                )
                                .flatMap(s -> s)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                )
                .build();
    }

    private OrderBoardSummaryData calculateBoardSummary(final List<OrderBoardData> boards,
                                                        final BigDecimal vatRate,
                                                        final OrderItemBoardSummaryData item) {
        final OrderBoardData board = boards.stream()
                .filter(b -> b.id().equals(item.id()))
                .findFirst()
                .orElseThrow();

        final BigDecimal boardArea = calculateArea(new DimensionsData(board.length(), board.width()));

        final BigDecimal boardsCount = calculateBoardsCount(boardArea, item.area());

        final BigDecimal price = calculatePrice(boardsCount, board.price());

        return OrderBoardSummaryData.builder()
                .id(item.id())
                .area(item.area())
                .boardsCount(boardsCount)
                .weight(calculateWeight(boardArea, item.area(), board.weight()))
                .price(price)
                .vatPrice(priceUtil.countVatValue(price, vatRate))
                .build();
    }

    private OrderEdgeSummaryData calculateEdgeSummary(final List<OrderEdgeData> edges,
                                                      final BigDecimal vatRate,
                                                      final List<PriceForGluingEdgeData> pricesForGluingEdge,
                                                      final OrderItemEdgeSummaryData item) {
        final OrderEdgeData edge = edges.stream()
                .filter(b -> b.id().equals(item.id()))
                .findFirst()
                .orElseThrow();

        final BigDecimal edgePrice = edge.price().multiply(item.length());

        final PriceForGluingEdgeData priceForGluingEdgeData = pricesForGluingEdge.stream()
                .filter(pr -> pr.width().compareTo(edge.width()) >= 0)
                .min(Comparator.comparing(PriceForGluingEdgeData::width))
                .orElseThrow();

        final BigDecimal gluePrice = priceForGluingEdgeData.price().multiply(item.glueLength());

        return OrderEdgeSummaryData.builder()
                .id(item.id())
                .length(item.length())
                .glueLength(item.glueLength())
                .weight(edge.weight().multiply(item.length()))
                .edgePrice(edgePrice)
                .edgeVatPrice(priceUtil.countVatValue(edgePrice, vatRate))
                .gluePrice(gluePrice)
                .glueVatPrice(priceUtil.countVatValue(gluePrice, vatRate))
                .build();
    }

    private OrderGlueSummaryData calculateGlueSummary(final BigDecimal vatRate,
                                                      final PriceForGluingLayerData priceForGluingLayer,
                                                      final BigDecimal area) {
        final BigDecimal price = area.multiply(priceForGluingLayer.price());
        return OrderGlueSummaryData.builder()
                .area(area)
                .price(price)
                .vatPrice(priceUtil.countVatValue(price, vatRate))
                .build();
    }

    private OrderCutSummaryData calculateCutSummary(final BigDecimal vatRate,
                                                    final List<PriceForCuttingData> pricesForCutting,
                                                    final OrderItemCutSummaryData item) {
        final PriceForCuttingData priceForCuttingData = pricesForCutting.stream()
                .filter(pr -> pr.thickness().compareTo(item.thickness()) >= 0)
                .min(Comparator.comparing(PriceForCuttingData::thickness))
                .orElseThrow();

        final BigDecimal price = priceForCuttingData.price().multiply(item.amount());

        return OrderCutSummaryData.builder()
                .thickness(item.thickness())
                .amount(item.amount())
                .price(price)
                .vatPrice(priceUtil.countVatValue(price, vatRate))
                .build();
    }

    private BigDecimal calculateBoardsCount(final BigDecimal boardArea, final BigDecimal area) {
        if (area.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return area
                .multiply(BigDecimal.valueOf(1.2))
                .divide(boardArea, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.UP);
    }

    private BigDecimal calculateWeight(final BigDecimal boardArea, final BigDecimal area, final BigDecimal weight) {
        if (area.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return area
                .divide(boardArea, new MathContext(PRECISION, RoundingMode.HALF_UP))
                .multiply(weight)
                .setScale(PRECISION, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePrice(final BigDecimal boardsCount, final BigDecimal price) {
        return boardsCount.multiply(price);
    }
}
