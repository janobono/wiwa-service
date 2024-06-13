package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.model.codelist.CodeListItemChangeData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemData;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSearchCriteriaData;
import sk.janobono.wiwa.business.service.CodeListItemService;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CodeListItemServiceImpl implements CodeListItemService {

    private final CodeListItemRepository codeListItemRepository;

    @Override
    public Page<CodeListItemData> getCodeListItems(final CodeListItemSearchCriteriaData criteria, final Pageable pageable) {
        return codeListItemRepository.findAll(mapToDo(criteria), pageable).map(this::toCodeListItemData);
    }

    @Override
    public CodeListItemData getCodeListItem(final long id) {
        return toCodeListItemData(getCodeListItemDo(id));
    }

    @Transactional
    @Override
    public CodeListItemData addCodeListItem(final CodeListItemChangeData data) {
        if (isCodeUsed(null, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list item code {0} is used", data.code());
        }

        final Optional<CodeListItemDo> parentCodeListItem = getParentCodeListItem(data.parentId());
        return toCodeListItemData(codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(data.codeListId())
                .parentId(data.parentId())
                .treeCode(getItemTreeCode(parentCodeListItem, data.code()))
                .code(data.code())
                .value(data.value())
                .sortNum(getNextSortNum(data.codeListId(), parentCodeListItem))
                .build())
        );
    }

    @Transactional
    @Override
    public CodeListItemData setCodeListItem(final long id, final CodeListItemChangeData data) {
        if (isCodeUsed(id, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list item code {0} is used", data.code());
        }

        CodeListItemDo codeListItemDo = getCodeListItemDo(id);

        final Optional<CodeListItemDo> parent = getParentCodeListItem(data.parentId());

        final Long previousParentId = codeListItemDo.getParentId();
        final boolean parentChanged = !Objects.equals(previousParentId, data.parentId());

        codeListItemDo.setParentId(data.parentId());
        if (parentChanged) {
            codeListItemDo.setTreeCode(getItemTreeCode(parent, data.code()));
        }
        codeListItemDo.setCode(data.code());
        codeListItemDo.setValue(data.value());
        if (parentChanged) {
            codeListItemDo.setSortNum(getNextSortNum(codeListItemDo.getCodeListId(), parent));
        }

        codeListItemDo = codeListItemRepository.save(codeListItemDo);

        if (parentChanged) {
            sortItems(codeListItemDo.getCodeListId(), previousParentId);
        }

        return toCodeListItemData(codeListItemRepository.save(codeListItemDo));
    }

    @Transactional
    @Override
    public void deleteCodeListItem(final long id) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id);
        if (!isLeafItem(id)) {
            throw WiwaException.CODE_LIST_ITEM_NOT_EMPTY.exception("Code list item with id {0} not empty", id);
        }
        codeListItemRepository.deleteById(id);
        sortItems(codeListItemDo.getCodeListId(), codeListItemDo.getParentId());
    }

    @Transactional
    @Override
    public CodeListItemData moveCodeListItemUp(final long id) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id);
        sortItems(codeListItemDo.getCodeListId(), codeListItemDo.getParentId());

        final List<CodeListItemDo> children = getItems(codeListItemDo.getCodeListId(), codeListItemDo.getParentId());
        final int categoryIndex = children.indexOf(codeListItemDo);
        if (categoryIndex > 0) {
            final CodeListItemDo upItem = children.get(categoryIndex);
            upItem.setSortNum(upItem.getSortNum() - 1);
            codeListItemRepository.save(upItem);

            final CodeListItemDo downItem = children.get(categoryIndex - 1);
            downItem.setSortNum(downItem.getSortNum() + 1);
            codeListItemRepository.save(downItem);
        }

        return toCodeListItemData(getCodeListItemDo(id));
    }

    @Transactional
    @Override
    public CodeListItemData moveCodeListItemDown(final long id) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id);
        sortItems(codeListItemDo.getCodeListId(), codeListItemDo.getParentId());

        final List<CodeListItemDo> children = getItems(codeListItemDo.getCodeListId(), codeListItemDo.getParentId());
        final int categoryIndex = children.indexOf(codeListItemDo);
        if (categoryIndex < children.size() - 1) {
            final CodeListItemDo downItem = children.get(categoryIndex);
            downItem.setSortNum(downItem.getSortNum() + 1);
            codeListItemRepository.save(downItem);

            final CodeListItemDo upItem = children.get(categoryIndex + 1);
            upItem.setSortNum(downItem.getSortNum() - 1);
            codeListItemRepository.save(upItem);
        }

        return toCodeListItemData(getCodeListItemDo(id));
    }

    private CodeListItemDo getCodeListItemDo(final long id) {
        return codeListItemRepository.findById(id)
                .orElseThrow(() -> WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Code list item with id {0} not found", id));
    }

    private boolean isCodeUsed(final Long id, final CodeListItemChangeData data) {
        return Optional.ofNullable(id)
                .map(id_ -> codeListItemRepository.countByIdNotAndCode(id_, data.code()) > 0)
                .orElseGet(() -> codeListItemRepository.countByCode(data.code()) > 0);
    }

    private Optional<CodeListItemDo> getParentCodeListItem(final Long parentId) {
        final Optional<CodeListItemDo> parentCategory;
        if (Optional.ofNullable(parentId).isPresent()) {
            final CodeListItemDo codeListItemDo = codeListItemRepository.findById(parentId)
                    .orElseThrow(() -> WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Code list item with id {1} not found", parentId));
            parentCategory = Optional.of(codeListItemDo);
        } else {
            parentCategory = Optional.empty();
        }
        return parentCategory;
    }

    private CodeListItemData toCodeListItemData(final CodeListItemDo codeListCodeDo) {
        return new CodeListItemData(
                codeListCodeDo.getId(),
                codeListCodeDo.getCodeListId(),
                codeListCodeDo.getSortNum(),
                codeListCodeDo.getCode(),
                codeListCodeDo.getValue(),
                isLeafItem(codeListCodeDo.getId())
        );
    }

    private boolean isLeafItem(final Long id) {
        return codeListItemRepository.countByParentId(id) == 0;
    }

    private String getItemTreeCode(final Optional<CodeListItemDo> parent, final String code) {
        return parent
                .map(category -> category.getTreeCode() + "::" + code)
                .orElse(code);
    }

    private Integer getNextSortNum(final Long id, final Optional<CodeListItemDo> parent) {
        return parent
                .map(category -> codeListItemRepository.countByParentId(category.getId()))
                .orElseGet(() -> codeListItemRepository.countByCodeListIdAndParentIdNull(id));
    }

    private void sortItems(final Long id, final Long parentItemId) {
        final List<CodeListItemDo> items = getItems(id, parentItemId);
        for (final CodeListItemDo item : items) {
            item.setSortNum(items.indexOf(item));
            codeListItemRepository.save(item);
        }
    }

    private List<CodeListItemDo> getItems(final Long id, final Long parentItemId) {
        final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.ASC, "sortNum");
        final CodeListItemSearchCriteriaData criteria = Optional.ofNullable(parentItemId)
                .map(parentCategoryId -> CodeListItemSearchCriteriaData.builder()
                        .codeListId(id)
                        .parentId(parentCategoryId)
                        .build())
                .orElseGet(() -> CodeListItemSearchCriteriaData.builder()
                        .codeListId(id)
                        .root(true)
                        .build());
        return codeListItemRepository.findAll(mapToDo(criteria), pageable).stream()
                .toList();
    }

    private CodeListItemSearchCriteriaDo mapToDo(final CodeListItemSearchCriteriaData criteria) {
        return new CodeListItemSearchCriteriaDo(
                criteria.codeListId(),
                criteria.root(),
                criteria.parentId(),
                criteria.searchField(),
                criteria.code(),
                criteria.value(),
                criteria.treeCode()
        );
    }
}
