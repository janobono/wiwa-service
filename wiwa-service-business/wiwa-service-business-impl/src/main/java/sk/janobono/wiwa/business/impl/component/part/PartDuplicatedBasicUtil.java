package sk.janobono.wiwa.business.impl.component.part;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;
import sk.janobono.wiwa.model.BoardPosition;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartDuplicatedBasicUtil extends BasePartUtil<PartDuplicatedBasicData> {

    @Override
    public void validate(final PartDuplicatedBasicData part,
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
            checkMin(part.dimensions().get(boardPosition), min, part.orientation());

            final DimensionsData maxBoard = getBoardDimensions(part.boards().get(boardPosition), orderBoards)
                    .subtract(manufactureProperties.duplicatedBoardAppend());
            checkMax(part.dimensions().get(boardPosition), maxBoard, part.orientation());
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

    @Override
    public Map<BoardPosition, DimensionsData> calculateBoardDimensions(final PartDuplicatedBasicData part,
                                                                       final ManufacturePropertiesData manufactureProperties) {
        return part.dimensions().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
                        entry.getValue().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<BigDecimal, BigDecimal> calculateCutLength(final PartDuplicatedBasicData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        addDimensions(
                boardThickness.get(part.boardId()),
                cutMap,
                part.dimensionsTOP()
                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
        );

        addDimensions(
                boardThickness.get(part.boardIdBottom()),
                cutMap,
                part.dimensionsTOP()
                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
        );

        addDimensions(
                boardThickness.get(part.boardId())
                        .add(boardThickness.get(part.boardIdBottom())),
                cutMap,
                part.dimensionsTOP()
        );

        for (final PartCornerData corner : part.corners().values()) {
            addValue(
                    boardThickness.get(part.boardId())
                            .add(boardThickness.get(part.boardIdBottom())),
                    cutMap,
                    calculateCornerLength(corner)
            );
        }

        return cutMap;
    }
}
