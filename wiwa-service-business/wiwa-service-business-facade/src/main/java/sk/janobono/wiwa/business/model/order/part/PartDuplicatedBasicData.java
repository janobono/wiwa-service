package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;
import sk.janobono.wiwa.business.model.DimensionsData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
public record PartDuplicatedBasicData(
        Boolean rotate,
        Long boardId,
        Long boardIdBottom,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        DimensionsData dimensions,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2
) implements PartData {

    @Override
    public Map<BoardPosition, Long> boards() {
        final Map<BoardPosition, Long> boards = new HashMap<>();
        boards.put(BoardPosition.TOP, boardId);
        boards.put(BoardPosition.BOTTOM, boardIdBottom);
        return boards;
    }

    @Override
    public Map<EdgePosition, Long> edges() {
        final Map<EdgePosition, Long> edgePositions = new HashMap<>();
        Optional.ofNullable(edgeIdA1).ifPresent(id -> edgePositions.put(EdgePosition.A1, id));
        Optional.ofNullable(edgeIdA2).ifPresent(id -> edgePositions.put(EdgePosition.A2, id));
        Optional.ofNullable(edgeIdB1).ifPresent(id -> edgePositions.put(EdgePosition.B1, id));
        Optional.ofNullable(edgeIdB2).ifPresent(id -> edgePositions.put(EdgePosition.B2, id));
        return edgePositions;
    }

    @Override
    public Map<CornerPosition, DimensionsData> corners() {
        final Map<CornerPosition, DimensionsData> cornerPositions = new HashMap<>();
        Optional.ofNullable(cornerA1B1).ifPresent(corner -> cornerPositions.put(CornerPosition.A1B1, corner.dimensions()));
        Optional.ofNullable(cornerA1B2).ifPresent(corner -> cornerPositions.put(CornerPosition.A1B2, corner.dimensions()));
        Optional.ofNullable(cornerA2B1).ifPresent(corner -> cornerPositions.put(CornerPosition.A2B1, corner.dimensions()));
        Optional.ofNullable(cornerA2B2).ifPresent(corner -> cornerPositions.put(CornerPosition.A2B2, corner.dimensions()));
        return cornerPositions;
    }
}
