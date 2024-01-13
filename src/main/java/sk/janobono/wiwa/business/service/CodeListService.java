package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.codelist.CodeListDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CodeListService {

    private final CodeListRepository codeListRepository;

    public Page<CodeListSo> getCodeLists(final CodeListSearchCriteriaSo criteria, final Pageable pageable) {
        return codeListRepository.findAll(criteria, pageable).map(this::toCodeListSo);
    }

    public CodeListSo getCodeList(final Long id) {
        return toCodeListSo(getCodeListDo(id));
    }

    public CodeListSo addCodeList(final CodeListDataSo data) {
        if (isCodeUsed(null, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list code {0} is used", data.code());
        }

        return toCodeListSo(codeListRepository.save(CodeListDo.builder()
                .code(data.code())
                .name(data.name())
                .build())
        );
    }

    public CodeListSo setCodeList(final Long id, final CodeListDataSo data) {
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

    private boolean isCodeUsed(final Long id, final CodeListDataSo data) {
        return Optional.ofNullable(id)
                .map(codeListId -> codeListRepository.countByIdNotAndCode(codeListId, data.code()) > 0)
                .orElseGet(() -> codeListRepository.countByCode(data.code()) > 0);
    }

    private CodeListSo toCodeListSo(final CodeListDo codeListDo) {
        return new CodeListSo(codeListDo.getId(), codeListDo.getCode(), codeListDo.getName());
    }
}
