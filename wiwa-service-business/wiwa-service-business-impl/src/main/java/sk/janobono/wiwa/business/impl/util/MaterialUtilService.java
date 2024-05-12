package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MaterialUtilService {

    private final BoardCodeListItemRepository boardCodeListItemRepository;

    public Map<Long, String> getMaterialNames(final List<OrderBoardData> boards, final BoardCategoryData boardMaterialCategory, final String notFoundName) {
        final Map<Long, String> result = new HashMap<>();

        for (final OrderBoardData board : boards) {
            final String value = boardCodeListItemRepository.findByBoardId(board.id()).stream()
                    .filter(cat -> Objects.equals(cat.getCodeListId(), boardMaterialCategory.id()))
                    .findFirst()
                    .map(CodeListItemDo::getValue)
                    .orElse(notFoundName);
            result.put(board.id(), value);
        }

        return result;
    }

    public Optional<OrderBoardData> findBoard(final List<OrderBoardData> boards, final long boardId) {
        return boards.stream().filter(board -> board.id().equals(boardId)).findFirst();
    }

    public Optional<OrderEdgeData> findEdge(final List<OrderEdgeData> edges, final long edgeId) {
        return edges.stream().filter(edge -> edge.id().equals(edgeId)).findFirst();
    }

    public String getDecor(final List<OrderBoardData> boards, final long boardId, final String notFoundName) {
        final Optional<OrderBoardData> board = findBoard(boards, boardId);
        return board.map(OrderBoardData::boardCode).orElse(notFoundName) + " " +
                board.map(OrderBoardData::structureCode).orElse(notFoundName) + " " +
                board.map(OrderBoardData::name).orElse(notFoundName);
    }

    public String getEdge(final String format, final List<OrderEdgeData> edges, final Long edgeId, final String notFoundName) {
        if (edgeId == null) {
            return "";
        }
        final Optional<OrderEdgeData> edge = findEdge(edges, edgeId);
        return format.formatted(
                edge.map(OrderEdgeData::code).orElse(notFoundName),
                edge.map(OrderEdgeData::width).map(BigDecimal::intValue).orElse(0),
                edge.map(OrderEdgeData::thickness).map(BigDecimal::doubleValue).orElse(0d)
        );
    }
}
