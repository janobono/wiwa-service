package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;

public interface CodeListService {

    Page<CodeListData> getCodeLists(CodeListSearchCriteriaData criteria, Pageable pageable);

    CodeListData getCodeList(long id);

    CodeListData addCodeList(CodeListChangeData data);

    CodeListData setCodeList(long id, CodeListChangeData data);

    void deleteCodeList(long id);
}
