package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.CodeListItemDo;

import java.util.List;

public interface BoardCodeListItemRepository {

    List<CodeListItemDo> findByBoardId(final Long boardId);

    void saveAll(final Long boardId, final List<Long> itemIds);
}
