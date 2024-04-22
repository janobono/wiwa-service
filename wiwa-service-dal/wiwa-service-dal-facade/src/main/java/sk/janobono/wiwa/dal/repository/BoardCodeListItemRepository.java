package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.CodeListItemDo;

import java.util.List;

public interface BoardCodeListItemRepository {

    List<CodeListItemDo> findByBoardId(long boardId);

    void saveAll(long boardId, List<Long> itemIds);
}
