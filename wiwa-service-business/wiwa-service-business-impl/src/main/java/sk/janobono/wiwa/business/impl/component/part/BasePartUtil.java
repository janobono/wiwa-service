package sk.janobono.wiwa.business.impl.component.part;

import sk.janobono.wiwa.business.impl.component.BaseCalculationUtil;
import sk.janobono.wiwa.business.impl.model.summary.EdgeLengthData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.FrameType;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BasePartUtil<P extends PartData> extends BaseCalculationUtil {

    public abstract void validate(P part,
                                  Map<Long, OrderBoardData> orderBoards,
                                  Map<Long, OrderEdgeData> orderEdges,
                                  ManufacturePropertiesData manufactureProperties);

    public abstract Map<BoardPosition, DimensionsData> calculateBoardDimensions(P part,
                                                                                ManufacturePropertiesData manufactureProperties);

    public abstract Map<BigDecimal, BigDecimal> calculateCutLength(P part,
                                                                   Map<Long, BigDecimal> boardThickness,
                                                                   ManufacturePropertiesData manufactureProperties);

    public Map<BoardPosition, BigDecimal> calculateBoardArea(final P part,
                                                             final ManufacturePropertiesData manufactureProperties) {
        return calculateBoardDimensions(part, manufactureProperties).entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), calculateArea(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Long, EdgeLengthData> calculateEdgeLength(final P part,
                                                         final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, EdgeLengthData> edgeLengthMap = new HashMap<>();
        final Map<BoardPosition, DimensionsData> dimensions = part.dimensions();

        for (final Map.Entry<EdgePosition, Long> edgeEntry : part.edges().entrySet()) {
            final Long edgeId = edgeEntry.getValue();
            final EdgeLengthData edgeLength = edgeLengthMap.getOrDefault(edgeId, new EdgeLengthData(BigDecimal.ZERO, BigDecimal.ZERO));
            edgeLengthMap.put(edgeId, switch (edgeEntry.getKey()) {
                case A1, A2 ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.TOP).x(), manufactureProperties);
                case B1, B2 ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.TOP).y(), manufactureProperties);
                case A1I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.A1).x(), manufactureProperties);
                case A2I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.A2).x(), manufactureProperties);
                case B1I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.B1).y(), manufactureProperties);
                case B2I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.B2).y(), manufactureProperties);
                case A1B1 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A1B1), manufactureProperties);
                case A1B2 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A1B2), manufactureProperties);
                case A2B1 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A2B1), manufactureProperties);
                case A2B2 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A2B2), manufactureProperties);
            });
        }

        return edgeLengthMap;
    }

    protected void checkParams(final P part,
                               final Set<BoardPosition> boardPositions,
                               final Map<Long, OrderBoardData> orderBoards,
                               final Map<Long, OrderEdgeData> orderEdges) {
        final Map<BoardPosition, DimensionsData> dimensions = part.dimensions();
        checkKeys(boardPositions, dimensions);

        final Map<BoardPosition, Long> boards = part.boards();
        checkKeys(boardPositions, boards);

        final Map<EdgePosition, Long> edges = part.edges();

        checkMaterial(new HashSet<>(boards.values()), orderBoards);
        checkMaterial(new HashSet<>(edges.values()), orderEdges);
    }

    protected <T> void checkKeys(final Set<T> keys, final Map<T, ?> map) {
        for (final T key : keys) {
            if (!map.containsKey(key)) {
                throw WiwaException.ORDER_ITEM_PROPERTIES.exception("Missing property {0}", key);
            }
        }
    }

    protected void checkMaterial(final Set<Long> ids, final Map<Long, ?> map) {
        for (final Long id : ids) {
            if (!map.containsKey(id)) {
                throw WiwaException.ORDER_ITEM_PROPERTIES.exception("Missing material {0}", id);
            }
        }
    }

    protected DimensionsData getBoardDimensions(final Long boardId, final Map<Long, OrderBoardData> orderBoards) {
        return new DimensionsData(orderBoards.get(boardId).length(), orderBoards.get(boardId).width());
    }

    protected void checkDimensions(final DimensionsData dimensions,
                                   final DimensionsData min,
                                   final DimensionsData max,
                                   final boolean rotate) {
        checkMin(dimensions, min, rotate);
        checkMax(dimensions, max, rotate);
    }

    protected void checkMin(final DimensionsData dimensions, final DimensionsData min, final boolean rotate) {
        if (dimensions.x().compareTo(min.x()) >= 0 && dimensions.y().compareTo(min.y()) >= 0) {
            return;
        }
        if (rotate && dimensions.x().compareTo(min.y()) >= 0 && dimensions.y().compareTo(min.x()) >= 0) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Invalid dimensions {0} minimum is {1}", dimensions, min);
    }

    protected void checkMax(final DimensionsData dimensions, final DimensionsData max, final boolean rotate) {
        if (dimensions.x().compareTo(max.x()) <= 0 && dimensions.y().compareTo(max.y()) <= 0) {
            return;
        }
        if (rotate && dimensions.x().compareTo(max.y()) <= 0 && dimensions.y().compareTo(max.x()) <= 0) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Invalid dimensions {0} maximum is {1}", dimensions, max);
    }

    protected void checkEdges(final Map<EdgePosition, Long> edges,
                              final Map<Long, OrderEdgeData> orderEdges,
                              final BigDecimal minWidth) {
        for (final Long edgeId : edges.values()) {
            final BigDecimal edgeWidth = orderEdges.get(edgeId).width();
            if (minWidth.compareTo(edgeWidth) > 0) {
                throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge width [{0}] minimum is {1}",
                        edgeWidth, minWidth);
            }
        }
    }

    protected void checkCorners(final Map<CornerPosition, PartCornerData> corners,
                                final DimensionsData dimensions) {
        if (corners.isEmpty()) {
            return;
        }
        final PartCornerData empty = new PartCornerData() {
            @Override
            public Long edgeId() {
                return -1L;
            }

            @Override
            public DimensionsData dimensions() {
                return new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
            }
        };
        checkCorner(dimensions.x(), corners.getOrDefault(CornerPosition.A1B1, empty).dimensions().x()
                .add(corners.getOrDefault(CornerPosition.A1B2, empty).dimensions().x()));
        checkCorner(dimensions.x(), corners.getOrDefault(CornerPosition.A2B1, empty).dimensions().x()
                .add(corners.getOrDefault(CornerPosition.A2B2, empty).dimensions().x()));
        checkCorner(dimensions.y(), corners.getOrDefault(CornerPosition.A1B1, empty).dimensions().y()
                .add(corners.getOrDefault(CornerPosition.A2B1, empty).dimensions().y()));
        checkCorner(dimensions.y(), corners.getOrDefault(CornerPosition.A1B2, empty).dimensions().y()
                .add(corners.getOrDefault(CornerPosition.A2B2, empty).dimensions().y()));
    }

    protected void checkCorner(final BigDecimal max, final BigDecimal sum) {
        if (max.compareTo(sum) < 0) {
            throw WiwaException.ORDER_ITEM_PART_CORNER.exception("Invalid corner dimensions");
        }
    }

    protected void checkThickness(final Set<BoardPosition> boardPositions,
                                  final Map<BoardPosition, Long> boards,
                                  final Map<Long, OrderBoardData> orderBoards) {
        if (boardPositions.stream()
                .map(boards::get)
                .map(orderBoards::get)
                .map(OrderBoardData::thickness)
                .distinct().count() <= 1) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Invalid board thickness");
    }

    protected BigDecimal getThickness(final Set<BoardPosition> boardPositions,
                                      final Map<BoardPosition, Long> boards,
                                      final Map<Long, OrderBoardData> orderBoards) {
        return boardPositions.stream()
                .map(boards::get)
                .map(orderBoards::get)
                .map(OrderBoardData::thickness)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    protected BigDecimal sumThickness(final Set<BoardPosition> boardPositions,
                                      final Map<BoardPosition, Long> boards,
                                      final Map<Long, OrderBoardData> orderBoards) {
        return boardPositions.stream()
                .map(boards::get)
                .map(orderBoards::get)
                .map(OrderBoardData::thickness)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected EdgeLengthData calculateEdgeLength(final EdgeLengthData edgeLength,
                                                 final BigDecimal augend,
                                                 final ManufacturePropertiesData manufactureProperties) {
        return new EdgeLengthData(
                calculateLength(edgeLength, augend),
                calculateConsumption(edgeLength, augend, manufactureProperties)
        );
    }

    protected EdgeLengthData calculateEdgeLength(final EdgeLengthData edgeLength,
                                                 final PartCornerData partCorner,
                                                 final ManufacturePropertiesData manufactureProperties) {
        final BigDecimal augend = calculateCornerLength(partCorner);

        return new EdgeLengthData(
                calculateLength(edgeLength, augend),
                calculateConsumption(edgeLength, augend, manufactureProperties)
        );
    }

    protected BigDecimal calculateLength(final EdgeLengthData edgeLength, final BigDecimal augend) {
        return edgeLength.length().add(millimeterToMeter(augend));
    }

    protected BigDecimal calculateConsumption(final EdgeLengthData edgeLength,
                                              final BigDecimal augend,
                                              final ManufacturePropertiesData manufactureProperties) {
        return edgeLength.consumption().add(millimeterToMeter(augend.add(manufactureProperties.edgeLengthAppend())));
    }

    protected void addDimensions(final BigDecimal thickness,
                                 final Map<BigDecimal, BigDecimal> cutMap,
                                 final DimensionsData dimensionsData) {
        final BigDecimal length = cutMap.getOrDefault(thickness, BigDecimal.ZERO);
        cutMap.put(thickness, length.add(calculatePerimeter(dimensionsData)));
    }

    protected void addValue(final BigDecimal thickness,
                            final Map<BigDecimal, BigDecimal> cutMap,
                            final BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        final BigDecimal length = cutMap.getOrDefault(thickness, BigDecimal.ZERO);
        cutMap.put(thickness, length.add(millimeterToMeter(value)));
    }

    protected void validateFrameDimensions(final FrameType frameType,
                                           final DimensionsData top,
                                           final Map<BoardPosition, DimensionsData> dimensions) {
        if (frameType == FrameType.VERTICAL) {
            // X
            if (dimensions.containsKey(BoardPosition.A1)) {
                final BigDecimal a1x = dimensions.get(BoardPosition.A1).x()
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
                if (top.x().compareTo(a1x) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.x is not valid");
                }
            }
            if (dimensions.containsKey(BoardPosition.A2)) {
                final BigDecimal a2x = dimensions.get(BoardPosition.A2).x()
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
                if (top.x().compareTo(a2x) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A2.x is not valid");
                }
            }

            // Y
            if (dimensions.containsKey(BoardPosition.B1)) {
                if (top.y().compareTo(dimensions.get(BoardPosition.B1).y()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.y is not valid");
                }
            }

            if (dimensions.containsKey(BoardPosition.B2)) {
                if (top.y().compareTo(dimensions.get(BoardPosition.B2).y()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B2.y is not valid");
                }
            }

            final BigDecimal sumY = Optional.ofNullable(dimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(dimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
            if (top.y().compareTo(sumY) < 0) {
                throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.y + A2.y is bigger than Y");
            }
        } else {
            // X
            if (dimensions.containsKey(BoardPosition.A1)) {
                if (top.x().compareTo(dimensions.get(BoardPosition.A1).x()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.x is not valid");
                }
            }

            if (dimensions.containsKey(BoardPosition.A2)) {
                if (top.x().compareTo(dimensions.get(BoardPosition.A2).x()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A2.x is not valid");
                }
            }

            final BigDecimal sumX = Optional.ofNullable(dimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(dimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
            if (top.x().compareTo(sumX) < 0) {
                throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.x + B2.x is bigger than X");
            }

            // Y
            if (dimensions.containsKey(BoardPosition.B1)) {
                final BigDecimal b1y = dimensions.get(BoardPosition.B1).y()
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
                if (top.y().compareTo(b1y) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.y is not valid");
                }
            }
            if (dimensions.containsKey(BoardPosition.B2)) {
                final BigDecimal b2y = dimensions.get(BoardPosition.B2).y()
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(dimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
                if (top.y().compareTo(b2y) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B2.y is not valid");
                }
            }
        }
    }

    protected void validateFrameBoards(final FrameType frameType,
                                       final Map<BoardPosition, Long> boards,
                                       final Map<BoardPosition, DimensionsData> dimensions,
                                       final DimensionsData min,
                                       final BigDecimal materialAugend,
                                       final Map<Long, OrderBoardData> orderBoards) {
        for (final BoardPosition boardPosition : boards.keySet()) {
            final DimensionsData d = switch (boardPosition) {
                case A1, A2: {
                    if (frameType == FrameType.HORIZONTAL_SHORT) {
                        yield dimensions.get(boardPosition).rotate();
                    } else {
                        yield dimensions.get(boardPosition);
                    }
                }
                case B1, B2: {
                    if (frameType == FrameType.HORIZONTAL_LONG) {
                        yield dimensions.get(boardPosition).rotate();
                    } else {
                        yield dimensions.get(boardPosition);
                    }
                }
                default:
                    throw new RuntimeException("Illegal board position: " + boardPosition);
            };
            checkDimensions(
                    d,
                    min,
                    getBoardDimensions(boards.get(boardPosition), orderBoards).subtract(materialAugend),
                    false
            );
        }
    }
}
