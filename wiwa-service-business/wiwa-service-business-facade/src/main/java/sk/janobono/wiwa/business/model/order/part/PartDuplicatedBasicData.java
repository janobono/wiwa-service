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
        DimensionsData dimensionsTOP,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2
) implements PartData {

    @Override
    public Map<BoardPosition, DimensionsData> dimensions() {
        final Map<BoardPosition, DimensionsData> dimensions = new HashMap<>();
        Optional.ofNullable(dimensionsTOP).ifPresent(d -> dimensions.put(BoardPosition.TOP, d));
        Optional.ofNullable(dimensionsTOP).ifPresent(d -> dimensions.put(BoardPosition.BOTTOM, d));
        return dimensions;
    }

    @Override
    public Map<BoardPosition, Long> boards() {
        final Map<BoardPosition, Long> boards = new HashMap<>();
        Optional.ofNullable(boardId).ifPresent(id -> boards.put(BoardPosition.TOP, id));
        Optional.ofNullable(boardIdBottom).ifPresent(id -> boards.put(BoardPosition.BOTTOM, id));
        return boards;
    }

    @Override
    public Map<EdgePosition, Long> edges() {
        final Map<EdgePosition, Long> edges = new HashMap<>();
        Optional.ofNullable(edgeIdA1).ifPresent(id -> edges.put(EdgePosition.A1, id));
        Optional.ofNullable(edgeIdA2).ifPresent(id -> edges.put(EdgePosition.A2, id));
        Optional.ofNullable(edgeIdB1).ifPresent(id -> edges.put(EdgePosition.B1, id));
        Optional.ofNullable(edgeIdB2).ifPresent(id -> edges.put(EdgePosition.B2, id));
        return edges;
    }

    @Override
    public Map<CornerPosition, DimensionsData> corners() {
        final Map<CornerPosition, DimensionsData> corners = new HashMap<>();
        Optional.ofNullable(cornerA1B1).ifPresent(corner -> corners.put(CornerPosition.A1B1, corner.dimensions()));
        Optional.ofNullable(cornerA1B2).ifPresent(corner -> corners.put(CornerPosition.A1B2, corner.dimensions()));
        Optional.ofNullable(cornerA2B1).ifPresent(corner -> corners.put(CornerPosition.A2B1, corner.dimensions()));
        Optional.ofNullable(cornerA2B2).ifPresent(corner -> corners.put(CornerPosition.A2B2, corner.dimensions()));
        return corners;
    }
}
