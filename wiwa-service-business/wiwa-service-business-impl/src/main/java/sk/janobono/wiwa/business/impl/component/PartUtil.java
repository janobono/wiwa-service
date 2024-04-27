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
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class PartUtil {

    public void validate(final PartData part,
                         final Map<Long, OrderBoardData> boards,
                         final Map<Long, OrderEdgeData> edges,
                         final ManufacturePropertiesData manufactureProperties) {
        switch (part) {
            case final PartBasicData partBasic -> validateBasic(partBasic, boards, edges, manufactureProperties);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    validateDuplicatedBasic(partDuplicatedBasic, boards, edges, manufactureProperties);
            case final PartFrameData partFrame -> validateFrame(partFrame, boards, edges, manufactureProperties);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    validatePartDuplicatedFrame(partDuplicatedFrame, boards, edges, manufactureProperties);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

    private void validateBasic(final PartBasicData part,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {
        // Part and top board
        checkDimensions(part.dimensions(),
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boardId(), boards),
                part.rotate());

        // Edges
        checkEdges(boards.get(part.boardId()).thickness(),
                new HashSet<>(part.edges().values()),
                edges,
                manufactureProperties.edgeWidthAppend());

        // Corners
        checkCorners(part.corners(), part.dimensions());
    }

    private void validateDuplicatedBasic(final PartDuplicatedBasicData part,
                                         final Map<Long, OrderBoardData> boards,
                                         final Map<Long, OrderEdgeData> edges,
                                         final ManufacturePropertiesData manufactureProperties) {
        // Part
        final DimensionsData min = manufactureProperties.minimalSystemDimensions()
                .add(manufactureProperties.duplicatedBoardAppend());
        checkMin(part.dimensions(), min, part.rotate());

        // Top board
        final DimensionsData maxBoard = getBoardDimensions(part.boardId(), boards)
                .subtract(manufactureProperties.duplicatedBoardAppend());
        checkMax(part.dimensions(), maxBoard, part.rotate());

        // Bottom board
        final DimensionsData maxBoardBottom = getBoardDimensions(part.boardIdBottom(), boards)
                .subtract(manufactureProperties.duplicatedBoardAppend());
        checkMax(part.dimensions(), maxBoardBottom, part.rotate());

        // Edges
        final BigDecimal thickness = boards.get(part.boardId()).thickness()
                .add(boards.get(part.boardIdBottom()).thickness());
        checkEdges(thickness,
                new HashSet<>(part.edges().values()),
                edges,
                manufactureProperties.edgeWidthAppend());

        // Corners
        checkCorners(part.corners(), part.dimensions());
    }

    private void validateFrame(final PartFrameData part,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {
        // All boards thickness check
        checkThickness(new HashSet<>(part.boards().values()), boards);

        // Prepare dimensions
        final DimensionsData dimensionsB1;
        final DimensionsData dimensionsB2;
        if (part.frameType() == FrameType.HORIZONTAL_LONG) {
            dimensionsB1 = part.dimensionsB1().rotate();
            dimensionsB2 = part.dimensionsB2().rotate();
        } else {
            dimensionsB1 = part.dimensionsB1();
            dimensionsB2 = part.dimensionsB2();
        }

        // Part
        if (part.frameType() == FrameType.VERTICAL) {
            final BigDecimal x1 = part.dimensionsB1().x().add(part.dimensionsA1().x()).add(part.dimensionsB2().x());
            final BigDecimal x2 = part.dimensionsB1().x().add(part.dimensionsA2().x()).add(part.dimensionsB2().x());
            if (part.dimensions().x().compareTo(x1) != 0 || part.dimensions().x().compareTo(x2) != 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Dimension X is not valid");
            }
            if (part.dimensions().y().compareTo(part.dimensionsB1().y()) != 0 || part.dimensions().y().compareTo(part.dimensionsB2().y()) != 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Dimension Y is not valid");
            }
            final BigDecimal sumY = part.dimensionsA1().y().add(part.dimensionsA2().y());
            if (part.dimensions().y().compareTo(sumY) < 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("A1.y + A2.y more than Y");
            }
        } else {
            if (part.dimensions().x().compareTo(part.dimensionsA1().x()) != 0 && part.dimensions().x().compareTo(part.dimensionsA2().x()) != 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Dimension X is not valid");
            }
            final BigDecimal sumX = part.dimensionsB1().x().add(part.dimensionsB2().x());
            if (part.dimensions().x().compareTo(sumX) < 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("B1.x + B2.x more than X");
            }
            final BigDecimal y1 = part.dimensionsA1().y().add(part.dimensionsB1().y()).add(part.dimensionsA2().y());
            final BigDecimal y2 = part.dimensionsA1().y().add(part.dimensionsB2().y()).add(part.dimensionsA2().y());
            if (part.dimensions().y().compareTo(y1) != 0 || part.dimensions().y().compareTo(y2) != 0) {
                throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Dimension Y is not valid");
            }
        }

        // A1 board
        checkDimensions(part.dimensionsA1(),
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boardIdA1(), boards),
                false);

        // A2 board
        checkDimensions(part.dimensionsA2(),
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boardIdA2(), boards),
                false);

        // B1 board
        checkDimensions(dimensionsB1,
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boardIdB1(), boards),
                false);

        // B2 board
        checkDimensions(dimensionsB2,
                manufactureProperties.minimalSystemDimensions(),
                getBoardDimensions(part.boardIdB2(), boards),
                false);

        // Edges
        checkEdges(boards.get(part.boardIdA1()).thickness(),
                new HashSet<>(part.edges().values()),
                edges,
                manufactureProperties.edgeWidthAppend());
    }

    private void validatePartDuplicatedFrame(final PartDuplicatedFrameData part,
                                             final Map<Long, OrderBoardData> boards,
                                             final Map<Long, OrderEdgeData> edges,
                                             final ManufacturePropertiesData manufactureProperties) {
        // Top board check + corners
        validateBasic(PartBasicData.builder()
                        .rotate(part.rotate())
                        .boardId(part.boardId())
                        .dimensions(part.dimensions().add(manufactureProperties.duplicatedBoardAppend()))
                        .cornerA1B1(part.cornerA1B1())
                        .cornerA1B2(part.cornerA1B2())
                        .cornerA2B1(part.cornerA2B1())
                        .cornerA2B2(part.cornerA2B2())
                        .build(),
                boards,
                edges,
                manufactureProperties);

        // Frame dimensions check
        final Map<BoardPosition, Long> frameBoards = part.boards().entrySet().stream()
                .filter(entry -> switch (entry.getKey()) {
                    case BoardPosition.TOP, BoardPosition.BOTTOM -> false;
                    default -> true;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (frameBoards.isEmpty()) {
            throw WiwaException.ORDER_ITEM_PART_BOARD.exception("Minimum one frame board is required");
        }
        checkThickness(new HashSet<>(frameBoards.values()), boards);
        final BigDecimal bottomThickness = boards.get(frameBoards.values().stream().findFirst().orElse(-1L)).thickness();

        final Map<EdgePosition, Long> edgePositions = part.edges();
        if ((edgePositions.containsKey(EdgePosition.A1I) && !frameBoards.containsKey(BoardPosition.A1)) ||
                (edgePositions.containsKey(EdgePosition.A2I) && !frameBoards.containsKey(BoardPosition.A2)) ||
                (edgePositions.containsKey(EdgePosition.B1I) && !frameBoards.containsKey(BoardPosition.B1)) ||
                (edgePositions.containsKey(EdgePosition.B2I) && !frameBoards.containsKey(BoardPosition.B2))
        ) {
            throw WiwaException.ORDER_ITEM_PART_EDGE.exception("Edge defined for not defined board");
        }

        // A1
throw new RuntimeException("Not implemented yet");
        // A2

        // B1

        // B2

        // EDGES
    }

    private DimensionsData getBoardDimensions(final Long boardId, final Map<Long, OrderBoardData> boards) {
        return new DimensionsData(boards.get(boardId).length(), boards.get(boardId).width());
    }

    private void checkDimensions(final DimensionsData dimensions, final DimensionsData min, final DimensionsData max, final boolean rotate) {
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
        throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Invalid dimensions {0} minimum is {1}", dimensions, min);
    }

    private void checkMax(final DimensionsData dimensions, final DimensionsData max, final boolean rotate) {
        if (dimensions.x().compareTo(max.x()) <= 0 && dimensions.y().compareTo(max.y()) <= 0) {
            return;
        }
        if (rotate && dimensions.x().compareTo(max.y()) <= 0 && dimensions.y().compareTo(max.x()) <= 0) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_DIMENSION.exception("Invalid dimensions {0} maximum is {1}", dimensions, max);
    }

    private void checkEdges(final BigDecimal partThickness,
                            final Set<Long> edgeIds,
                            final Map<Long, OrderEdgeData> edges,
                            final BigDecimal edgeWidthAppend) {
        for (final Long edgeId : edgeIds) {
            final BigDecimal neededWidth = partThickness.add(edgeWidthAppend);
            final BigDecimal edgeWidth = edges.get(edgeId).width();
            if (neededWidth.compareTo(edgeWidth) > 0) {
                throw WiwaException.ORDER_ITEM_PART_EDGE_WIDTH.exception("Invalid edge width [{0}] minimum is {1}",
                        edgeWidth, neededWidth);
            }
        }
    }

    private void checkCorners(final Map<CornerPosition, DimensionsData> cornerPositions, final DimensionsData dimensions) {
        if (cornerPositions.isEmpty()) {
            return;
        }
        final DimensionsData empty = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        checkCorner(dimensions.x(), cornerPositions.getOrDefault(CornerPosition.A1B1, empty).x().add(cornerPositions.getOrDefault(CornerPosition.A1B2, empty).x()));
        checkCorner(dimensions.x(), cornerPositions.getOrDefault(CornerPosition.A2B1, empty).x().add(cornerPositions.getOrDefault(CornerPosition.A2B2, empty).x()));
        checkCorner(dimensions.y(), cornerPositions.getOrDefault(CornerPosition.A1B1, empty).y().add(cornerPositions.getOrDefault(CornerPosition.A2B1, empty).y()));
        checkCorner(dimensions.y(), cornerPositions.getOrDefault(CornerPosition.A1B2, empty).y().add(cornerPositions.getOrDefault(CornerPosition.A2B2, empty).y()));
    }

    private void checkCorner(final BigDecimal max, final BigDecimal sum) {
        if (max.compareTo(sum) < 0) {
            throw WiwaException.ORDER_ITEM_PART_CORNER_DIMENSION.exception("Invalid corner dimensions");
        }
    }

    private void checkThickness(final Set<Long> ids, final Map<Long, OrderBoardData> boards) {
        if (ids.stream()
                .map(boards::get)
                .map(OrderBoardData::thickness)
                .distinct().count() <= 1) {
            return;
        }
        throw WiwaException.ORDER_ITEM_PART_THICKNESS.exception("Invalid board thickness");
    }
}
