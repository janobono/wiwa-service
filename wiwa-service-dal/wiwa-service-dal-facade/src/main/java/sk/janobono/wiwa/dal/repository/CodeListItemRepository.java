package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;

import java.util.List;
import java.util.Optional;

public interface CodeListItemRepository {

    int countByCode(String code);

    int countByCodeListIdAndParentIdNull(Long codeListId);

    int countByIdNotAndCode(Long id, String code);

    int countByParentId(Long parentId);

    boolean existsById(Long id);

    void deleteById(Long id);

    Page<CodeListItemDo> findAll(CodeListItemSearchCriteriaDo criteria, Pageable pageable);

    Optional<CodeListItemDo> findById(Long id);

    CodeListItemDo save(CodeListItemDo codeListItemDo);

    void saveAll(List<CodeListItemDo> batch);
}
