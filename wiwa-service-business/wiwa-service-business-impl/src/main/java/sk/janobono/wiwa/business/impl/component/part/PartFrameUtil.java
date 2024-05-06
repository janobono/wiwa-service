package sk.janobono.wiwa.business.impl.component.part;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;
import sk.janobono.wiwa.model.BoardPosition;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartFrameUtil extends BasePartUtil<PartFrameData> {

    @Override
    public void validate(final PartFrameData part,
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
        validateFrameDimensions(part.frameType(), part.dimensionsTOP(), calculateBoardDimensions(part, manufactureProperties));

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

    @Override
    public Map<BoardPosition, DimensionsData> calculateBoardDimensions(final PartFrameData part,
                                                                       final ManufacturePropertiesData manufactureProperties) {
        return part.dimensions().entrySet().stream()
                .filter(entry -> switch (entry.getKey()) {
                    case A1, A2, B1, B2 -> true;
                    default -> false;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<BigDecimal, BigDecimal> calculateCutLength(final PartFrameData part,
                                                          final Map<Long, BigDecimal> boardThickness,
                                                          final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        for (final Map.Entry<BoardPosition, Long> boardEntry : part.boards().entrySet()) {
            final BigDecimal thickness = boardThickness.get(boardEntry.getValue());
            addDimensions(thickness, cutMap, part.dimensions().get(boardEntry.getKey()));
        }

        return cutMap;
    }
}
