package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.CodeListItemWebMapper;
import sk.janobono.wiwa.api.model.codelist.CodeListItemChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListItemWebDto;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSearchCriteriaData;
import sk.janobono.wiwa.business.service.CodeListItemService;

@RequiredArgsConstructor
@Service
public class CodeListItemApiService {

    private final CodeListItemService codeListItemService;
    private final CodeListItemWebMapper codeListItemWebMapper;

    public Page<CodeListItemWebDto> getCodeListItems(final Long codeListId, final Boolean root, final Long parentId,
                                                     final String searchField, final String code, final String value,
                                                     final String treeCode, final Pageable pageable) {
        final CodeListItemSearchCriteriaData criteria = CodeListItemSearchCriteriaData.builder()
                .codeListId(codeListId)
                .root(root)
                .parentId(parentId)
                .searchField(searchField)
                .code(code)
                .value(value)
                .treeCode(treeCode)
                .build();

        return codeListItemService.getCodeListItems(criteria, pageable)
                .map(codeListItemWebMapper::mapToWebDto);
    }

    public CodeListItemWebDto getCodeListItem(final Long id) {
        return codeListItemWebMapper.mapToWebDto(codeListItemService.getCodeListItem(id));
    }

    public CodeListItemWebDto addCodeListItem(final CodeListItemChangeWebDto codeListItemChange) {
        return codeListItemWebMapper.mapToWebDto(codeListItemService.addCodeListItem(codeListItemWebMapper.mapToData(codeListItemChange)));
    }

    public CodeListItemWebDto setCodeListItem(final Long id, final CodeListItemChangeWebDto codeListItemChange) {
        return codeListItemWebMapper.mapToWebDto(codeListItemService.setCodeListItem(id, codeListItemWebMapper.mapToData(codeListItemChange)));
    }

    public void deleteCodeListItem(final Long id) {
        codeListItemService.deleteCodeListItem(id);
    }

    public CodeListItemWebDto moveCodeListItemUp(final Long id) {
        return codeListItemWebMapper.mapToWebDto(codeListItemService.moveCodeListItemUp(id));
    }

    public CodeListItemWebDto moveCodeListItemDown(final Long id) {
        return codeListItemWebMapper.mapToWebDto(codeListItemService.moveCodeListItemDown(id));
    }
}
