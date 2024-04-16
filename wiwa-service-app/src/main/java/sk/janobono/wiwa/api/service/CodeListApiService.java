package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.CodeListWebMapper;
import sk.janobono.wiwa.api.model.codelist.CodeListChangeWebDto;
import sk.janobono.wiwa.api.model.codelist.CodeListWebDto;
import sk.janobono.wiwa.business.model.codelist.CodeListSearchCriteriaData;
import sk.janobono.wiwa.business.service.CodeListService;

@RequiredArgsConstructor
@Service
public class CodeListApiService {

    private final CodeListService codeListService;
    private final CodeListWebMapper codeListWebMapper;

    public Page<CodeListWebDto> getCodeLists(final String searchField, final String code, final String name, final Pageable pageable) {
        final CodeListSearchCriteriaData criteria = CodeListSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .build();

        return codeListService.getCodeLists(criteria, pageable)
                .map(codeListWebMapper::mapToWebDto);
    }

    public CodeListWebDto getCodeList(final Long id) {
        return codeListWebMapper.mapToWebDto(codeListService.getCodeList(id));
    }

    public CodeListWebDto addCodeList(final CodeListChangeWebDto data) {
        return codeListWebMapper.mapToWebDto(codeListService.addCodeList(codeListWebMapper.mapToData(data)));
    }

    public CodeListWebDto setCodeList(final Long id, final CodeListChangeWebDto codeListChange) {
        return codeListWebMapper.mapToWebDto(codeListService.setCodeList(id, codeListWebMapper.mapToData(codeListChange)));
    }

    public void deleteCodeList(final Long id) {
        codeListService.deleteCodeList(id);
    }
}
