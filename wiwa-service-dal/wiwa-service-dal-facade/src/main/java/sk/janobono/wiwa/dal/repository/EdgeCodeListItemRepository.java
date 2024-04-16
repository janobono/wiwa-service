package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.CodeListItemDo;

import java.util.List;

public interface EdgeCodeListItemRepository {

    List<CodeListItemDo> findByEdgeId(final Long edgeId);

    void saveAll(final Long edgeId, final List<Long> itemIds);
}
