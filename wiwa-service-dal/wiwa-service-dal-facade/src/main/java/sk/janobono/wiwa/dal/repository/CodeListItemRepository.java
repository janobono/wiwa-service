package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;

import java.util.List;
import java.util.Optional;

public interface CodeListItemRepository {

    int countByCode(final String code);

    int countByCodeListIdAndParentIdNull(final Long codeListId);

    int countByIdNotAndCode(final Long id, final String code);

    int countByParentId(final Long parentId);

    boolean existsById(final Long id);

    void deleteById(final Long id);

    Page<CodeListItemDo> findAll(final CodeListItemSearchCriteriaDo criteria, final Pageable pageable);

    Optional<CodeListItemDo> findById(final Long id);

    CodeListItemDo save(final CodeListItemDo codeListItemDo);

    void saveAll(final List<CodeListItemDo> batch);
}
