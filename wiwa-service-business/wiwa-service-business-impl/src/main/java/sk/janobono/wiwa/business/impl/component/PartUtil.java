package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.FrameType;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PartUtil {

    public void validate(final PartData part,
                         final Map<Long, OrderBoardData> orderBoards,
                         final Map<Long, OrderEdgeData> orderEdges,
                         final ManufacturePropertiesData manufactureProperties) {
        switch (part) {
            case final PartBasicData partBasic ->
                    validateBasic(partBasic, orderBoards, orderEdges, manufactureProperties);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    validateDuplicatedBasic(partDuplicatedBasic, orderBoards, orderEdges, manufactureProperties);
            case final PartFrameData partFrame ->
                    validateFrame(partFrame, orderBoards, orderEdges, manufactureProperties);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    validatePartDuplicatedFrame(partDuplicatedFrame, orderBoards, orderEdges, manufactureProperties);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

    private void validateBasic(final PartBasicData part,
                               final Map<Long, OrderBoardData> orderBoards,
                               final Map<Long, OrderEdgeData> orderEdges,
                               final ManufacturePropertiesData manufactureProperties) {
        // Params
        final Set<BoardPosition> boardPositions = Set.of(BoardPosition.TOP);
        checkParams(part, boardPositions, orderBoards, orderEdges);

        // Boards
        checkDimensions(
                part.dimensions().get(BoardPosition.TOP),
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boards().get(BoardPosition.TOP), orderBoards),
                part.rotate()
        );

        // Edges
        final BigDecimal partThickness = sumThickness(boardPositions, part.boards(), orderBoards);
        checkEdges(
                part.edges(),
                orderEdges,
                partThickness.add(manufactureProperties.edgeWidthAppend())
        );

        // Corners
        checkCorners(part.corners(), part.dimensions().get(BoardPosition.TOP));
    }

    private void validateDuplicatedBasic(final PartDuplicatedBasicData part,
                                         final Map<Long, OrderBoardData> orderBoards,
                                         final Map<Long, OrderEdgeData> orderEdges,
                                         final ManufacturePropertiesData manufactureProperties) {
        // Params
        final Set<BoardPosition> boardPositions = Set.of(BoardPosition.TOP, BoardPosition.BOTTOM);
        checkParams(part, boardPositions, orderBoards, orderEdges);

        // Boards
        final DimensionsData min = manufactureProperties.minimalSystemDimensions()
                .add(manufactureProperties.duplicatedBoardAppend());
        for (final BoardPosition boardPosition : boardPositions) {
            checkMin(part.dimensions().get(boardPosition), min, part.rotate());

            final DimensionsData maxBoard = getBoardDimensions(part.boards().get(boardPosition), orderBoards)
                    .subtract(manufactureProperties.duplicatedBoardAppend());
            checkMax(part.dimensions().get(boardPosition), maxBoard, part.rotate());
        }

        // Edges
        final BigDecimal partThickness = sumThickness(boardPositions, part.boards(), orderBoards);
        checkEdges(
                part.edges(),
                orderEdges,
                partThickness.add(manufactureProperties.edgeWidthAppend())
        );

        // Corners
        checkCorners(part.corners(), part.dimensions().get(BoardPosition.TOP));
    }

    private void validateFrame(final PartFrameData part,
                               final Map<Long, OrderBoardData> orderBoards,
                               final Map<Long, OrderEdgeData> orderEdges,
                               final ManufacturePropertiesData manufactureProperties) {
        // Params
        checkKeys(Set.of(BoardPosition.TOP), part.dimensions());
        final Set<BoardPosition> boardPositions = Set.of(BoardPosition.A1, BoardPosition.A2, BoardPosition.B1, BoardPosition.B2);
        checkParams(part, boardPositions, orderBoards, orderEdges);

        // Frame boards thickness check
        checkThickness(boardPositions, part.boards(), orderBoards);

        // Frame dimensions
        validateFrameDimensions(part.frameType(), part.dimensions());

        // Frame boards
        validateFrameBoards(
                part.frameType(),
                part.boards(),
                part.dimensions(),
                manufactureProperties.minimalSystemDimensions(),
                BigDecimal.ZERO,
                orderBoards
        );

        // Edges
        final BigDecimal partThickness = getThickness(boardPositions, part.boards(), orderBoards);
        checkEdges(
                part.edges(),
                orderEdges,
                partThickness.add(manufactureProperties.edgeWidthAppend())
        );
    }

    private void validatePartDuplicatedFrame(final PartDuplicatedFrameData part,
                                             final Map<Long, OrderBoardData> orderBoards,
                                             final Map<Long, OrderEdgeData> orderEdges,
                                             final ManufacturePropertiesData manufactureProperties) {
        // Top
        validateBasic(
                PartBasicData.builder()
                        .rotate(part.rotate())
                        .boardId(part.boardId())
                        .dimensionsTOP(Optional.ofNullable(part.dimensionsTOP()).map(t -> t.add(manufactureProperties.duplicatedBoardAppend())).orElse(null))
                        .cornerA1B1(part.cornerA1B1())
                        .cornerA1B2(part.cornerA1B2())
                        .cornerA2B1(part.cornerA2B1())
                        .cornerA2B2(part.cornerA2B2())
                        .build(),
                orderBoards,
                orderEdges,
                manufactureProperties
        );

        // Bottom

        // Params
        final Set<BoardPosition> frameBoardPositions = part.boards().keySet().stream()
                .filter(key -> switch (key) {
                    case BoardPosition.TOP, BoardPosition.BOTTOM -> false;
                    default -> true;
                })
                .collect(Collectors.toSet());
        checkParams(part, frameBoardPositions, orderBoards, orderEdges);

        // Frame boards thickness check
        checkThickness(frameBoardPositions, part.boards(), orderBoards);

        // Frame dimensions
        validateFrameDimensions(part.frameType(), part.dimensions());

        // Frame boards
        validateFrameBoards(
                part.frameType(),
                part.boards().entrySet().stream()
                        .filter(entry -> switch (entry.getKey()) {
                            case TOP, BOTTOM -> false;
                            default -> true;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                part.dimensions(),
                manufactureProperties.minimalSystemDimensions().add(manufactureProperties.duplicatedBoardAppend()),
                manufactureProperties.duplicatedBoardAppend(),
                orderBoards
        );

        // Edges
        final Map<EdgePosition, Long> edges = part.edges();
        for (final EdgePosition edgePosition : edges.keySet()) {
            final BigDecimal thickness = switch (edgePosition) {
                case A1 -> {
                    if (!part.boards().containsKey(BoardPosition.A1)
                            && (part.boards().containsKey(BoardPosition.B1) || part.boards().containsKey(BoardPosition.B2))) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield sumThickness(Set.of(BoardPosition.TOP, BoardPosition.A1), part.boards(), orderBoards);
                }
                case A1I -> {
                    if (!part.boards().containsKey(BoardPosition.A1)) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield getThickness(Set.of(BoardPosition.A1), part.boards(), orderBoards);
                }
                case A2 -> {
                    if (!part.boards().containsKey(BoardPosition.A2)
                            && (part.boards().containsKey(BoardPosition.B1) || part.boards().containsKey(BoardPosition.B2))) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield sumThickness(Set.of(BoardPosition.TOP, BoardPosition.A2), part.boards(), orderBoards);
                }
                case A2I -> {
                    if (!part.boards().containsKey(BoardPosition.A2)) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield getThickness(Set.of(BoardPosition.A2), part.boards(), orderBoards);
                }
                case B1 -> {
                    if (!part.boards().containsKey(BoardPosition.B1)
                            && (part.boards().containsKey(BoardPosition.A1) || part.boards().containsKey(BoardPosition.A2))) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield sumThickness(Set.of(BoardPosition.TOP, BoardPosition.B1), part.boards(), orderBoards);
                }
                case B1I -> {
                    if (!part.boards().containsKey(BoardPosition.B1)) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield getThickness(Set.of(BoardPosition.B1), part.boards(), orderBoards);
                }
                case B2 -> {
                    if (!part.boards().containsKey(BoardPosition.B2)
                            && (part.boards().containsKey(BoardPosition.A1) || part.boards().containsKey(BoardPosition.A2))) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield sumThickness(Set.of(BoardPosition.TOP, BoardPosition.B2), part.boards(), orderBoards);
                }
                case B2I -> {
                    if (!part.boards().containsKey(BoardPosition.B2)) {
                        throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Invalid edge position: " + edgePosition);
                    }
                    yield getThickness(Set.of(BoardPosition.B2), part.boards(), orderBoards);
                }
            };
            checkEdges(
                    Map.of(edgePosition, edges.get(edgePosition)),
                    orderEdges,
                    thickness
            );
        }
    }

    private void checkParams(final PartData part,
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

    private <T> void checkKeys(final Set<T> keys, final Map<T, ?> map) {
        for (final T key : keys) {
            if (!map.containsKey(key)) {
                throw WiwaException.ORDER_ITEM_PROPERTIES.exception("Missing property {0}", key);
            }
        }
    }

    private void checkMaterial(final Set<Long> ids, final Map<Long, ?> map) {
        for (final Long id : ids) {
            if (!map.containsKey(id)) {
                throw WiwaException.ORDER_ITEM_PROPERTIES.exception("Missing material {0}", id);
            }
        }
    }

    private DimensionsData getBoardDimensions(final Long boardId, final Map<Long, OrderBoardData> orderBoards) {
        return new DimensionsData(orderBoards.get(boardId).length(), orderBoards.get(boardId).width());
    }

    private void checkDimensions(final DimensionsData dimensions,
                                 final DimensionsData min,
                                 final DimensionsData max,
                                 final boolean rotate) {
        checkMin(dimensions, min, rotate);
        checkMax(dimensions, max, rotate);
    }

    private void checkMin(final DimensionsData dimensions, final DimensionsData min, final boolean rotate) {
        if (dimensions.x().compareTo(min.x()) >= 0 && dimensions.y().compareTo(min.y()) >= 0) {
            return;
        }
        if (rotate && dimensions.x().compareTo(min.y()) >= 0 && dimensions.y().compareTo(min.x()) >= 0) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Invalid dimensions {0} minimum is {1}", dimensions, min);
    }

    private void checkMax(final DimensionsData dimensions, final DimensionsData max, final boolean rotate) {
        if (dimensions.x().compareTo(max.x()) <= 0 && dimensions.y().compareTo(max.y()) <= 0) {
            return;
        }
        if (rotate && dimensions.x().compareTo(max.y()) <= 0 && dimensions.y().compareTo(max.x()) <= 0) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Invalid dimensions {0} maximum is {1}", dimensions, max);
    }

    private void checkEdges(final Map<EdgePosition, Long> edges,
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

    private void checkCorners(final Map<CornerPosition, DimensionsData> corners,
                              final DimensionsData dimensions) {
        if (corners.isEmpty()) {
            return;
        }
        final DimensionsData empty = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        checkCorner(dimensions.x(), corners.getOrDefault(CornerPosition.A1B1, empty).x()
                .add(corners.getOrDefault(CornerPosition.A1B2, empty).x()));
        checkCorner(dimensions.x(), corners.getOrDefault(CornerPosition.A2B1, empty).x()
                .add(corners.getOrDefault(CornerPosition.A2B2, empty).x()));
        checkCorner(dimensions.y(), corners.getOrDefault(CornerPosition.A1B1, empty).y()
                .add(corners.getOrDefault(CornerPosition.A2B1, empty).y()));
        checkCorner(dimensions.y(), corners.getOrDefault(CornerPosition.A1B2, empty).y()
                .add(corners.getOrDefault(CornerPosition.A2B2, empty).y()));
    }

    private void checkCorner(final BigDecimal max, final BigDecimal sum) {
        if (max.compareTo(sum) < 0) {
            throw WiwaException.ORDER_ITEM_PART_CORNER.exception("Invalid corner dimensions");
        }
    }

    private void checkThickness(final Set<BoardPosition> boardPositions,
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

    private BigDecimal getThickness(final Set<BoardPosition> boardPositions,
                                    final Map<BoardPosition, Long> boards,
                                    final Map<Long, OrderBoardData> orderBoards) {
        return boardPositions.stream()
                .map(boards::get)
                .map(orderBoards::get)
                .map(OrderBoardData::thickness)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal sumThickness(final Set<BoardPosition> boardPositions,
                                    final Map<BoardPosition, Long> boards,
                                    final Map<Long, OrderBoardData> orderBoards) {
        return boardPositions.stream()
                .map(boards::get)
                .map(orderBoards::get)
                .map(OrderBoardData::thickness)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateFrameDimensions(final FrameType frameType, final Map<BoardPosition, DimensionsData> dimensions) {
        final Map<BoardPosition, DimensionsData> frameDimensions = dimensions.entrySet().stream()
                .filter(entry -> switch (entry.getKey()) {
                    case BoardPosition.A1, BoardPosition.A2, BoardPosition.B1, BoardPosition.B2 -> true;
                    default -> false;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final DimensionsData top = dimensions.get(BoardPosition.TOP);

        if (frameType == FrameType.VERTICAL) {
            // X
            if (frameDimensions.containsKey(BoardPosition.A1)) {
                final BigDecimal a1x = frameDimensions.get(BoardPosition.A1).x()
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
                if (top.x().compareTo(a1x) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.x is not valid");
                }
            }
            if (frameDimensions.containsKey(BoardPosition.A2)) {
                final BigDecimal a2x = frameDimensions.get(BoardPosition.A2).x()
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
                if (top.x().compareTo(a2x) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A2.x is not valid");
                }
            }

            // Y
            if (frameDimensions.containsKey(BoardPosition.B1)) {
                if (top.y().compareTo(frameDimensions.get(BoardPosition.B1).y()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.y is not valid");
                }
            }

            if (frameDimensions.containsKey(BoardPosition.B2)) {
                if (top.y().compareTo(frameDimensions.get(BoardPosition.B2).y()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B2.y is not valid");
                }
            }

            final BigDecimal sumY = Optional.ofNullable(frameDimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(frameDimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
            if (top.y().compareTo(sumY) < 0) {
                throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.y + A2.y is bigger than Y");
            }
        } else {
            // X
            if (frameDimensions.containsKey(BoardPosition.A1)) {
                if (top.x().compareTo(frameDimensions.get(BoardPosition.A1).x()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A1.x is not valid");
                }
            }

            if (frameDimensions.containsKey(BoardPosition.A2)) {
                if (top.x().compareTo(frameDimensions.get(BoardPosition.A2).x()) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension A2.x is not valid");
                }
            }

            final BigDecimal sumX = Optional.ofNullable(frameDimensions.get(BoardPosition.B1)).map(DimensionsData::x).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(frameDimensions.get(BoardPosition.B2)).map(DimensionsData::x).orElse(BigDecimal.ZERO));
            if (top.x().compareTo(sumX) < 0) {
                throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.x + B2.x is bigger than X");
            }

            // Y
            if (frameDimensions.containsKey(BoardPosition.B1)) {
                final BigDecimal b1y = frameDimensions.get(BoardPosition.B1).y()
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
                if (top.y().compareTo(b1y) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B1.y is not valid");
                }
            }
            if (frameDimensions.containsKey(BoardPosition.B2)) {
                final BigDecimal b2y = frameDimensions.get(BoardPosition.B2).y()
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.A1)).map(DimensionsData::y).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(frameDimensions.get(BoardPosition.A2)).map(DimensionsData::y).orElse(BigDecimal.ZERO));
                if (top.y().compareTo(b2y) != 0) {
                    throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Dimension B2.y is not valid");
                }
            }
        }
    }

    private void validateFrameBoards(final FrameType frameType,
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
