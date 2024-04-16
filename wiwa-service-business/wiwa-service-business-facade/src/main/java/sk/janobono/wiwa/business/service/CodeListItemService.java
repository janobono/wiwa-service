package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.business.model.codelist.CodeListItemChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSearchCriteriaData;

public interface CodeListItemService {

    Page<CodeListItemData> getCodeListItems(final CodeListItemSearchCriteriaData criteria, final Pageable pageable);

    CodeListItemData getCodeListItem(final Long id);

    CodeListItemData addCodeListItem(final CodeListItemChangeData data);

    CodeListItemData setCodeListItem(final Long id, final CodeListItemChangeData data);

    void deleteCodeListItem(final Long id);

    CodeListItemData moveCodeListItemUp(final Long id);

    CodeListItemData moveCodeListItemDown(final Long id);
}
