package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;
import sk.janobono.wiwa.business.service.CodeListService;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CodeListServiceImpl implements CodeListService {

    private final CodeListRepository codeListRepository;

    @Override
    public Page<CodeListData> getCodeLists(final CodeListSearchCriteriaData criteria, final Pageable pageable) {
        return codeListRepository.findAll(mapToDo(criteria), pageable).map(this::toCodeListData);
    }

    @Override
    public CodeListData getCodeList(final long id) {
        return toCodeListData(getCodeListDo(id));
    }

    @Override
    public CodeListData addCodeList(final CodeListChangeData data) {
        if (isCodeUsed(null, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list code {0} is used", data.code());
        }

        return toCodeListData(codeListRepository.save(CodeListDo.builder()
                .code(data.code())
                .name(data.name())
                .build())
        );
    }

    @Override
    public CodeListData setCodeList(final long id, final CodeListChangeData data) {
        if (isCodeUsed(id, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list code {0} is used", data.code());
        }

        final CodeListDo codeListDo = getCodeListDo(id);
        codeListDo.setCode(data.code());
        codeListDo.setName(data.name());

        return toCodeListData(codeListRepository.save(codeListDo));
    }

    @Override
    public void deleteCodeList(final long id) {
        if (!codeListRepository.existsById(id)) {
            throw WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", id);
        }
        codeListRepository.deleteById(id);
    }

    private CodeListDo getCodeListDo(final long id) {
        return codeListRepository.findById(id)
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", id));
    }

    private boolean isCodeUsed(final Long id, final CodeListChangeData data) {
        return Optional.ofNullable(id)
                .map(codeListId -> codeListRepository.countByIdNotAndCode(codeListId, data.code()) > 0)
                .orElseGet(() -> codeListRepository.countByCode(data.code()) > 0);
    }

    private CodeListData toCodeListData(final CodeListDo codeListDo) {
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
