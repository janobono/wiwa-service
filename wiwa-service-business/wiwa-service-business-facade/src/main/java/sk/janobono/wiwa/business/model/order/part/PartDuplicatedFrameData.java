package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.model.FrameType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
public record PartDuplicatedFrameData(
        FrameType frameType,
        Boolean rotate,
        Long boardId,
        Long boardIdA1,
        Long boardIdA2,
        Long boardIdB1,
        Long boardIdB2,
        Long edgeIdA1,
        Long edgeIdA1I,
        Long edgeIdA2,
        Long edgeIdA2I,
        Long edgeIdB1,
        Long edgeIdB1I,
        Long edgeIdB2,
        Long edgeIdB2I,
        DimensionsData dimensions,
        DimensionsData dimensionsA1,
        DimensionsData dimensionsA2,
        DimensionsData dimensionsB1,
        DimensionsData dimensionsB2,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2
) implements PartData {

    @Override
    public Map<BoardPosition, Long> boards() {
        final Map<BoardPosition, Long> boards = new HashMap<>();
        boards.put(BoardPosition.TOP, boardId);
        boards.put(BoardPosition.A1, boardIdA1);
        boards.put(BoardPosition.A2, boardIdA2);
        boards.put(BoardPosition.B1, boardIdB1);
        boards.put(BoardPosition.B2, boardIdB2);
        return boards;
    }

    @Override
    public Map<EdgePosition, Long> edges() {
        final Map<EdgePosition, Long> edgePositions = new HashMap<>();
        Optional.ofNullable(edgeIdA1).ifPresent(id -> edgePositions.put(EdgePosition.A1, id));
        Optional.ofNullable(edgeIdA1I).ifPresent(id -> edgePositions.put(EdgePosition.A1I, id));
        Optional.ofNullable(edgeIdA2).ifPresent(id -> edgePositions.put(EdgePosition.A2, id));
        Optional.ofNullable(edgeIdA2I).ifPresent(id -> edgePositions.put(EdgePosition.A2I, id));
        Optional.ofNullable(edgeIdB1).ifPresent(id -> edgePositions.put(EdgePosition.B1, id));
        Optional.ofNullable(edgeIdB1I).ifPresent(id -> edgePositions.put(EdgePosition.B1I, id));
        Optional.ofNullable(edgeIdB2).ifPresent(id -> edgePositions.put(EdgePosition.B2, id));
        Optional.ofNullable(edgeIdB2I).ifPresent(id -> edgePositions.put(EdgePosition.B2I, id));
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
