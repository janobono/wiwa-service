package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.CodeListItemDo;

import java.util.List;

public interface EdgeCodeListItemRepository {

    List<CodeListItemDo> findByEdgeId(long edgeId);

    void saveAll(long edgeId, List<Long> itemIds);
}
