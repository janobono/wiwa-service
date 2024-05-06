package sk.janobono.wiwa.business.impl.component.part;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.model.BoardPosition;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PartBasicUtil extends BasePartUtil<PartBasicData> {

    @Override
    public void validate(final PartBasicData part,
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
                part.orientation()
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

    @Override
    public Map<BoardPosition, DimensionsData> calculateBoardDimensions(final PartBasicData part,
                                                                       final ManufacturePropertiesData manufactureProperties) {
        return part.dimensions();
    }

    @Override
    public Map<BigDecimal, BigDecimal> calculateCutLength(final PartBasicData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        addDimensions(
                boardThickness.get(part.boards().get(BoardPosition.TOP)),
                cutMap,
                part.dimensions().get(BoardPosition.TOP)
        );

        for (final PartCornerData corner : part.corners().values()) {
            addValue(
                    boardThickness.get(part.boards().get(BoardPosition.TOP)),
                    cutMap,
                    calculateCornerLength(corner)
            );
        }

        return cutMap;
    }
}
