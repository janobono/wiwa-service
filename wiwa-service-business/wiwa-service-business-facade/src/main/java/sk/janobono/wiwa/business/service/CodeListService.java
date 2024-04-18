package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;

public interface CodeListService {

    Page<CodeListData> getCodeLists(CodeListSearchCriteriaData criteria, Pageable pageable);

    CodeListData getCodeList(Long id);

    CodeListData addCodeList(CodeListChangeData data);

    CodeListData setCodeList(Long id, CodeListChangeData data);

    void deleteCodeList(Long id);
}
