package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CodeListService {

    private final CodeListRepository codeListRepository;

    public Page<CodeListData> getCodeLists(final CodeListSearchCriteriaData criteria, final Pageable pageable) {
        return codeListRepository.findAll(mapToDo(criteria), pageable).map(this::toCodeListSo);
    }

    public CodeListData getCodeList(final Long id) {
        return toCodeListSo(getCodeListDo(id));
    }

    public CodeListData addCodeList(final CodeListChangeData data) {
        if (isCodeUsed(null, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list code {0} is used", data.code());
        }

        return toCodeListSo(codeListRepository.save(CodeListDo.builder()
                .code(data.code())
                .name(data.name())
                .build())
        );
    }

    public CodeListData setCodeList(final Long id, final CodeListChangeData data) {
        if (isCodeUsed(id, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list code {0} is used", data.code());
        }

        final CodeListDo codeListDo = getCodeListDo(id);
        codeListDo.setCode(data.code());
        codeListDo.setName(data.name());

        return toCodeListSo(codeListRepository.save(codeListDo));
    }

    public void deleteCodeList(final Long id) {
        if (!codeListRepository.existsById(id)) {
            throw WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", id);
        }
        codeListRepository.deleteById(id);
    }

    private CodeListDo getCodeListDo(final Long id) {
        return codeListRepository.findById(id)
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", id));
    }

    private boolean isCodeUsed(final Long id, final CodeListChangeData data) {
        return Optional.ofNullable(id)
                .map(codeListId -> codeListRepository.countByIdNotAndCode(codeListId, data.code()) > 0)
                .orElseGet(() -> codeListRepository.countByCode(data.code()) > 0);
    }

    private CodeListData toCodeListSo(final CodeListDo codeListDo) {
        return new CodeListData(codeListDo.getId(), codeListDo.getCode(), codeListDo.getName());
    }

    private CodeListSearchCriteriaDo mapToDo(final CodeListSearchCriteriaData criteria) {
        return new CodeListSearchCriteriaDo(
                criteria.searchField(),
                criteria.code(),
                criteria.name()
        );
    }
}
