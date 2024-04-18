package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.codelist.CodeListItemChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSearchCriteriaData;

public interface CodeListItemService {

    Page<CodeListItemData> getCodeListItems(CodeListItemSearchCriteriaData criteria, Pageable pageable);

    CodeListItemData getCodeListItem(Long id);

    CodeListItemData addCodeListItem(CodeListItemChangeData data);

    CodeListItemData setCodeListItem(Long id, CodeListItemChangeData data);

    void deleteCodeListItem(Long id);

    CodeListItemData moveCodeListItemUp(Long id);

    CodeListItemData moveCodeListItemDown(Long id);
}
