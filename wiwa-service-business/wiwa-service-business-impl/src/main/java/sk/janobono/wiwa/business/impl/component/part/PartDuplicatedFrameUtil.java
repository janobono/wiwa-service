package sk.janobono.wiwa.business.impl.component.part;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.FrameType;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PartDuplicatedFrameUtil extends BasePartUtil<PartDuplicatedFrameData> {

    @Override
    public void validate(final PartDuplicatedFrameData part,
                         final Map<Long, OrderBoardData> orderBoards,
                         final Map<Long, OrderEdgeData> orderEdges,
                         final ManufacturePropertiesData manufactureProperties) {
        // Top
        new PartBasicUtil().validate(
                PartBasicData.builder()
                        .orientation(part.orientation())
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
        validateFrameDimensions(part.frameType(), part.dimensionsTOP(), part.dimensions());

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
                case A1, A1B1, A1B2 -> {
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
                case A2, A2B1, A2B2 -> {
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

    @Override
    public Map<BoardPosition, DimensionsData> calculateBoardDimensions(final PartDuplicatedFrameData part,
                                                                       final ManufacturePropertiesData manufactureProperties) {
        final Map<BoardPosition, DimensionsData> result = new HashMap<>();

        for (final BoardPosition boardPosition : part.boards().keySet()) {
            final DimensionsData boardDimensions = part.dimensions().get(boardPosition);
            result.put(boardPosition, switch (boardPosition) {
                case TOP -> calculateTopBoardDimensions(part, manufactureProperties, boardDimensions);
                case A1, A2 -> calculateADimensions(part, manufactureProperties, boardDimensions);
                case B1, B2 -> calculateBDimensions(part, manufactureProperties, boardDimensions);
                default -> throw new InvalidParameterException("Unsupported board position: " + boardPosition);
            });
        }

        return result;
    }

    @Override
    public Map<BigDecimal, BigDecimal> calculateCutLength(final PartDuplicatedFrameData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        final BigDecimal topLayerThickness = boardThickness.get(part.boardId());
        final BigDecimal bottomLayerThickness = boardThickness.get(part.boards().entrySet().stream()
                .filter(entry -> switch (entry.getKey()) {
                    case A1, A2, B1, B2 -> true;
                    default -> false;
                })
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(-1L));

        for (final BoardPosition boardPosition : part.boards().keySet()) {
            final DimensionsData boardDimensions = part.dimensions().get(boardPosition);
            switch (boardPosition) {
                case TOP -> addDimensions(topLayerThickness, cutMap,
                        calculateTopBoardDimensions(part, manufactureProperties, boardDimensions)
                );
                case A1, A2 -> addDimensions(bottomLayerThickness, cutMap,
                        calculateADimensions(part, manufactureProperties, boardDimensions)
                );
                case B1, B2 -> addDimensions(bottomLayerThickness, cutMap,
                        calculateBDimensions(part, manufactureProperties, boardDimensions)
                );
                default -> throw new InvalidParameterException("Unsupported board position: " + boardPosition);
            }
        }
        calculateFinalAndCornerLengths(part, topLayerThickness, bottomLayerThickness, cutMap);

        return cutMap;
    }

    private DimensionsData calculateADimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                final ManufacturePropertiesData manufactureProperties,
                                                final DimensionsData boardDimensions) {
        if (duplicatedFrame.frameType() == FrameType.VERTICAL) {
            BigDecimal x = boardDimensions.x();
            if (!duplicatedFrame.boards().containsKey(BoardPosition.B1)) {
                x = x.add(manufactureProperties.duplicatedBoardAppend());
            }
            if (!duplicatedFrame.boards().containsKey(BoardPosition.B2)) {
                x = x.add(manufactureProperties.duplicatedBoardAppend());
            }
            return new DimensionsData(
                    x,
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
            );
        } else {
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
            );
        }
    }

    private DimensionsData calculateBDimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                final ManufacturePropertiesData manufactureProperties,
                                                final DimensionsData boardDimensions) {
        if (duplicatedFrame.frameType() == FrameType.VERTICAL) {
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
            );
        } else {
            BigDecimal y = boardDimensions.y();
            if (!duplicatedFrame.boards().containsKey(BoardPosition.A1)) {
                y = y.add(manufactureProperties.duplicatedBoardAppend());
            }
            if (!duplicatedFrame.boards().containsKey(BoardPosition.A2)) {
                y = y.add(manufactureProperties.duplicatedBoardAppend());
            }
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                    y
            );
        }
    }

    private DimensionsData calculateTopBoardDimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                       final ManufacturePropertiesData manufactureProperties,
                                                       final DimensionsData boardDimensions) {
        if (duplicatedFrame.boards().size() > 2) {
            return boardDimensions.add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO));
        } else {
            if (duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.A2)) {
                return new DimensionsData(
                        boardDimensions.x()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                        boardDimensions.y()
                                .add(manufactureProperties.duplicatedBoardAppend())
                );
            } else {
                return new DimensionsData(
                        boardDimensions.x()
                                .add(manufactureProperties.duplicatedBoardAppend()),
                        boardDimensions.y()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                );
            }
        }
    }

    private void calculateFinalAndCornerLengths(final PartDuplicatedFrameData duplicatedFrame,
                                                final BigDecimal topLayerThickness,
                                                final BigDecimal bottomLayerThickness,
                                                final Map<BigDecimal, BigDecimal> cutMap) {
        // A1, A2
        for (final BoardPosition boardPosition : Set.of(BoardPosition.A1, BoardPosition.A2)) {
            final BigDecimal finalLength = calculateFinalXLength(duplicatedFrame,
                    duplicatedFrame.dimensionsTOP().x(), boardPosition);
            final BigDecimal topLength = duplicatedFrame.dimensionsTOP().x().subtract(finalLength);
            addValue(topLayerThickness.add(bottomLayerThickness), cutMap, finalLength);
            addValue(topLayerThickness, cutMap, topLength);
        }
        // B1, B2
        for (final BoardPosition boardPosition : Set.of(BoardPosition.B1, BoardPosition.B2)) {
            final BigDecimal finalLength = calculateFinalYLength(duplicatedFrame,
                    duplicatedFrame.dimensionsTOP().y(), boardPosition);
            final BigDecimal topLength = duplicatedFrame.dimensionsTOP().y().subtract(finalLength);
            addValue(topLayerThickness.add(bottomLayerThickness), cutMap, finalLength);
            addValue(topLayerThickness, cutMap, topLength);
        }

        // CORNERS
        duplicatedFrame.corners().forEach((cornerPosition, cornerData) -> {
            if (isCornerFinal(duplicatedFrame, cornerPosition)) {
                addValue(topLayerThickness.add(bottomLayerThickness), cutMap, calculateCornerLength(cornerData));
            } else {
                addValue(topLayerThickness, cutMap, calculateCornerLength(cornerData));
            }
        });
    }

    private boolean isCornerFinal(final PartDuplicatedFrameData duplicatedFrame, final CornerPosition cornerPosition) {
        return switch (cornerPosition) {
            case A1B1 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.B1);
            case A1B2 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.B2);
            case A2B1 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A2) || duplicatedFrame.boards().containsKey(BoardPosition.B1);
            case A2B2 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A2) || duplicatedFrame.boards().containsKey(BoardPosition.B2);
        };
    }

    private BigDecimal calculateFinalXLength(final PartDuplicatedFrameData duplicatedFrame,
                                             final BigDecimal x,
                                             final BoardPosition boardPosition) {
        if (duplicatedFrame.boards().containsKey(boardPosition)) {
            return x;
        }
        final DimensionsData zero = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        return duplicatedFrame.dimensions().getOrDefault(BoardPosition.B1, zero).y()
                .add(duplicatedFrame.dimensions().getOrDefault(BoardPosition.B2, zero).y());
    }

    private BigDecimal calculateFinalYLength(final PartDuplicatedFrameData duplicatedFrame,
                                             final BigDecimal y,
                                             final BoardPosition boardPosition) {
        if (duplicatedFrame.boards().containsKey(boardPosition)) {
            return y;
        }
        final DimensionsData zero = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        return duplicatedFrame.dimensions().getOrDefault(BoardPosition.A1, zero).x()
                .add(duplicatedFrame.dimensions().getOrDefault(BoardPosition.A2, zero).x());
    }
}
