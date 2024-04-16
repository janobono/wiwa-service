package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.codelist.CodeListChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListData;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;

public interface CodeListService {

    Page<CodeListData> getCodeLists(final CodeListSearchCriteriaData criteria, final Pageable pageable);

    CodeListData getCodeList(final Long id);

    CodeListData addCodeList(final CodeListChangeData data);

    CodeListData setCodeList(final Long id, final CodeListChangeData data);

    void deleteCodeList(final Long id);
}
