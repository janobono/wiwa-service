package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.FrameType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Builder
public record PartDuplicatedFrameData(
        FrameType frameType,
        Boolean orientation,
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
        DimensionsData dimensionsTOP,
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
    public Map<BoardPosition, DimensionsData> dimensions() {
        final Map<BoardPosition, DimensionsData> dimensions = new HashMap<>();
        Optional.ofNullable(dimensionsTOP).ifPresent(d -> dimensions.put(BoardPosition.TOP, d));
        Optional.ofNullable(dimensionsA1).ifPresent(d -> dimensions.put(BoardPosition.A1, d));
        Optional.ofNullable(dimensionsA2).ifPresent(d -> dimensions.put(BoardPosition.A2, d));
        Optional.ofNullable(dimensionsB1).ifPresent(d -> dimensions.put(BoardPosition.B1, d));
        Optional.ofNullable(dimensionsB2).ifPresent(d -> dimensions.put(BoardPosition.B2, d));
        return dimensions;
    }

    @Override
    public Map<BoardPosition, Long> boards() {
        final Map<BoardPosition, Long> boards = new HashMap<>();
        Optional.ofNullable(boardId).ifPresent(id -> boards.put(BoardPosition.TOP, id));
        Optional.ofNullable(boardIdA1).ifPresent(id -> boards.put(BoardPosition.A1, id));
        Optional.ofNullable(boardIdA2).ifPresent(id -> boards.put(BoardPosition.A2, id));
        Optional.ofNullable(boardIdB1).ifPresent(id -> boards.put(BoardPosition.B1, id));
        Optional.ofNullable(boardIdB2).ifPresent(id -> boards.put(BoardPosition.B2, id));
        return boards;
    }

    @Override
    public Map<EdgePosition, Long> edges() {
        final Map<EdgePosition, Long> edges = new HashMap<>();
        Optional.ofNullable(edgeIdA1).ifPresent(id -> edges.put(EdgePosition.A1, id));
        Optional.ofNullable(edgeIdA1I).ifPresent(id -> edges.put(EdgePosition.A1I, id));
        Optional.ofNullable(edgeIdA2).ifPresent(id -> edges.put(EdgePosition.A2, id));
        Optional.ofNullable(edgeIdA2I).ifPresent(id -> edges.put(EdgePosition.A2I, id));
        Optional.ofNullable(edgeIdB1).ifPresent(id -> edges.put(EdgePosition.B1, id));
        Optional.ofNullable(edgeIdB1I).ifPresent(id -> edges.put(EdgePosition.B1I, id));
        Optional.ofNullable(edgeIdB2).ifPresent(id -> edges.put(EdgePosition.B2, id));
        Optional.ofNullable(edgeIdB2I).ifPresent(id -> edges.put(EdgePosition.B2I, id));

        Optional.ofNullable(cornerA1B1).map(PartCornerData::edgeId).ifPresent(id -> edges.put(EdgePosition.A1B1, id));
        Optional.ofNullable(cornerA1B2).map(PartCornerData::edgeId).ifPresent(id -> edges.put(EdgePosition.A1B2, id));
        Optional.ofNullable(cornerA2B1).map(PartCornerData::edgeId).ifPresent(id -> edges.put(EdgePosition.A2B1, id));
        Optional.ofNullable(cornerA2B2).map(PartCornerData::edgeId).ifPresent(id -> edges.put(EdgePosition.A2B2, id));
        return edges;
    }

    @Override
    public Map<CornerPosition, PartCornerData> corners() {
        final Map<CornerPosition, PartCornerData> corners = new HashMap<>();
        Optional.ofNullable(cornerA1B1).ifPresent(corner -> corners.put(CornerPosition.A1B1, corner));
        Optional.ofNullable(cornerA1B2).ifPresent(corner -> corners.put(CornerPosition.A1B2, corner));
        Optional.ofNullable(cornerA2B1).ifPresent(corner -> corners.put(CornerPosition.A2B1, corner));
        Optional.ofNullable(cornerA2B2).ifPresent(corner -> corners.put(CornerPosition.A2B2, corner));
        return corners;
    }
}
