package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;

import java.util.Optional;

public interface CodeListItemRepository {

    int countByCode(String code);

    int countByCodeListIdAndParentIdNull(long codeListId);

    int countByIdNotAndCode(long id, String code);

    int countByParentId(long parentId);

    boolean existsById(long id);

    void deleteById(long id);

    Page<CodeListItemDo> findAll(CodeListItemSearchCriteriaDo criteria, Pageable pageable);

    Optional<CodeListItemDo> findById(long id);

    CodeListItemDo save(CodeListItemDo codeListItemDo);
}
