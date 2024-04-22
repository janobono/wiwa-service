package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;

import java.util.Optional;

public interface CodeListRepository {

    int countByCode(String code);

    int countByIdNotAndCode(long id, String code);

    void deleteById(long id);

    boolean existsById(long id);

    Page<CodeListDo> findAll(CodeListSearchCriteriaDo criteria, Pageable pageable);

    Optional<CodeListDo> findById(long id);

    CodeListDo save(CodeListDo codeListDo);
}
