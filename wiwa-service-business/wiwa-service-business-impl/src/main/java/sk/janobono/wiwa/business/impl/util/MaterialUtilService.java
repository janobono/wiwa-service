package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.CategoryData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MaterialUtilService {

    private final BoardCodeListItemRepository boardCodeListItemRepository;

    public Map<Long, String> getMaterialNames(final List<OrderBoardData> boards, final CategoryData categoryData, final String notFoundName) {
        final Map<Long, String> result = new HashMap<>();

        for (final OrderBoardData board : Optional.ofNullable(boards).orElse(Collections.emptyList())) {
            final String value = boardCodeListItemRepository.findByBoardId(board.id()).stream()
                    .filter(codeListItem -> Objects.equals(codeListItem.getCodeListId(),
                            Optional.ofNullable(categoryData).map(CategoryData::id).orElse(null)))
                    .findFirst()
                    .map(CodeListItemDo::getValue)
                    .orElse(Optional.ofNullable(notFoundName).orElse(""));
            result.put(board.id(), value);
        }

        return result;
    }

    public Optional<OrderBoardData> findBoard(final List<OrderBoardData> boards, final long boardId) {
        return Optional.ofNullable(boards).stream().flatMap(Collection::stream).filter(board -> Objects.equals(board.id(), boardId)).findFirst();
    }

    public Optional<OrderEdgeData> findEdge(final List<OrderEdgeData> edges, final long edgeId) {
        return Optional.ofNullable(edges).stream().flatMap(Collection::stream).filter(edge -> Objects.equals(edge.id(), edgeId)).findFirst();
    }

    public String getDecor(final String pattern, final List<OrderBoardData> boards, final long boardId, final String notFoundName) {
        final Optional<OrderBoardData> board = findBoard(boards, boardId);
        return MessageFormat.format(
                Optional.ofNullable(pattern).orElse(""),
                board.map(OrderBoardData::boardCode).orElse(notFoundName),
                board.map(OrderBoardData::structureCode).orElse(notFoundName),
                board.map(OrderBoardData::name).orElse(notFoundName)
        );
    }

    public String getEdge(final String pattern, final List<OrderEdgeData> edges, final Long edgeId, final String notFoundName) {
        if (edgeId == null) {
            return "";
        }
        final Optional<OrderEdgeData> edge = findEdge(edges, edgeId);
        return MessageFormat.format(
                Optional.ofNullable(pattern).orElse(""),
                edge.map(OrderEdgeData::code).orElse(notFoundName),
                edge.map(OrderEdgeData::width).map(BigDecimal::intValue).orElse(0),
                edge.map(OrderEdgeData::thickness).map(BigDecimal::doubleValue).orElse(0d)
        );
    }
}
