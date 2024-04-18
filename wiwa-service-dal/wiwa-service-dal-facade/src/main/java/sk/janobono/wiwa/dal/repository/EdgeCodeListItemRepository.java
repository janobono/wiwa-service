package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.CodeListItemDo;

import java.util.List;

public interface EdgeCodeListItemRepository {

    List<CodeListItemDo> findByEdgeId(Long edgeId);

    void saveAll(Long edgeId, List<Long> itemIds);
}
