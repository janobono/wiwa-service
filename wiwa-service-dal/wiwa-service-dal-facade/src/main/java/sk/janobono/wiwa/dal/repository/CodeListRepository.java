package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;

import java.util.Optional;

public interface CodeListRepository {

    int countByCode(final String code);

    int countByIdNotAndCode(final Long id, final String code);

    void deleteById(final Long id);

    boolean existsById(final Long id);

    Page<CodeListDo> findAll(final CodeListSearchCriteriaDo criteria, final Pageable pageable);

    Optional<CodeListDo> findById(final Long id);

    CodeListDo save(final CodeListDo codeListDo);
}
