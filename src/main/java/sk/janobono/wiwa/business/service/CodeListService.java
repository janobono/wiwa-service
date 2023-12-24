package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.codelist.*;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CodeListService {

    private final CodeListRepository codeListRepository;
    private final CodeListItemRepository codeListItemRepository;

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

    public Page<CodeListItemSo> getCodeListItems(final CodeListItemSearchCriteriaSo criteria, final Pageable pageable) {
        return codeListItemRepository.findAll(criteria, pageable).map(this::toCodeListItemSo);
    }

    public CodeListItemSo getCodeListItem(final Long id, final Long itemId) {
        return toCodeListItemSo(getCodeListItemDo(id, itemId));
    }

    public CodeListItemSo addCodeListItem(final Long id, final CodeListItemDataSo data) {
        if (isCodeUsed(null, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list item code {0} is used", data.code());
        }

        final CodeListDo codeListDo = getCodeListDo(id);
        final Optional<CodeListItemDo> parentCodeListItem = getParentCodeListItem(data.parentId());
        return toCodeListItemSo(codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeListDo.getId())
                .parentId(data.parentId())
                .treeCode(getItemTreeCode(parentCodeListItem, data.code()))
                .code(data.code())
                .value(data.value())
                .sortNum(getNextSortNum(id, parentCodeListItem))
                .build())
        );
    }

    public CodeListItemSo setCodeListItem(final Long id, final Long itemId, final CodeListItemDataSo data) {
        if (isCodeUsed(itemId, data)) {
            throw WiwaException.CODE_IS_USED.exception("Code list item code {0} is used", data.code());
        }

        final CodeListDo codeListDo = getCodeListDo(id);

        CodeListItemDo codeListItemDo = getCodeListItemDo(id, itemId);

        final Optional<CodeListItemDo> parent = getParentCodeListItem(data.parentId());

        final Long previousParentId = codeListItemDo.getParentId();
        final boolean parentChanged = !Objects.equals(previousParentId, data.parentId());

        codeListItemDo.setCodeListId(codeListDo.getId());
        codeListItemDo.setParentId(data.parentId());
        if (parentChanged) {
            codeListItemDo.setTreeCode(getItemTreeCode(parent, data.code()));
        }
        codeListItemDo.setCode(data.code());
        codeListItemDo.setValue(data.value());
        if (parentChanged) {
            codeListItemDo.setSortNum(getNextSortNum(id, parent));
        }

        codeListItemDo = codeListItemRepository.save(codeListItemDo);

        if (parentChanged) {
            sortItems(id, previousParentId);
        }

        return toCodeListItemSo(codeListItemRepository.save(codeListItemDo));
    }

    public void deleteCodeListItem(final Long id, final Long itemId) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id, itemId);
        if (!isLeafItem(itemId)) {
            throw WiwaException.CODE_LIST_ITEM_NOT_EMPTY.exception("Code list item with id {0} not empty", itemId);
        }
        codeListItemRepository.deleteById(itemId);
        sortItems(id, codeListItemDo.getParentId());
    }

    public CodeListItemSo moveCodeListItemUp(final Long id, final Long itemId) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id, itemId);
        sortItems(id, codeListItemDo.getParentId());

        final List<CodeListItemDo> children = getItems(id, codeListItemDo.getParentId());
        final int categoryIndex = children.indexOf(codeListItemDo);
        if (categoryIndex > 0) {
            final List<CodeListItemDo> batch = new ArrayList<>();

            final CodeListItemDo upItem = children.get(categoryIndex);
            upItem.setSortNum(upItem.getSortNum() - 1);
            batch.add(upItem);

            final CodeListItemDo downItem = children.get(categoryIndex - 1);
            downItem.setSortNum(downItem.getSortNum() + 1);
            batch.add(downItem);

            codeListItemRepository.saveAll(batch);
        }

        return toCodeListItemSo(getCodeListItemDo(id, itemId));
    }

    public CodeListItemSo moveCodeListItemDown(final Long id, final Long itemId) {
        final CodeListItemDo codeListItemDo = getCodeListItemDo(id, itemId);
        sortItems(id, codeListItemDo.getParentId());

        final List<CodeListItemDo> children = getItems(id, codeListItemDo.getParentId());
        final int categoryIndex = children.indexOf(codeListItemDo);
        if (categoryIndex < children.size() - 1) {
            final List<CodeListItemDo> batch = new ArrayList<>();

            final CodeListItemDo downItem = children.get(categoryIndex);
            downItem.setSortNum(downItem.getSortNum() + 1);
            batch.add(downItem);

            final CodeListItemDo upItem = children.get(categoryIndex + 1);
            upItem.setSortNum(downItem.getSortNum() - 1);
            batch.add(upItem);

            codeListItemRepository.saveAll(batch);
        }

        return toCodeListItemSo(getCodeListItemDo(id, itemId));
    }

    private CodeListDo getCodeListDo(final Long id) {
        return codeListRepository.findById(id)
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", id));
    }

    private CodeListItemDo getCodeListItemDo(final Long id, final Long itemId) {
        return codeListItemRepository.findById(itemId)
                .filter(codeListCodeDo -> codeListCodeDo.getCodeListId().equals(id))
                .orElseThrow(() -> WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Code list item with code list id {0} and id {1} not found", id, itemId));
    }

    private boolean isCodeUsed(final Long id, final CodeListDataSo data) {
        return Optional.ofNullable(id)
                .map(codeListId -> codeListRepository.countByIdNotAndCode(codeListId, data.code()) > 0)
                .orElse(codeListRepository.countByCode(data.code()) > 0);
    }

    private boolean isCodeUsed(final Long itemId, final CodeListItemDataSo data) {
        return Optional.ofNullable(itemId)
                .map(id -> codeListItemRepository.countByIdNotAndCode(id, data.code()) > 0)
                .orElse(codeListItemRepository.countByCode(data.code()) > 0);
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

    private CodeListSo toCodeListSo(final CodeListDo codeListDo) {
        return new CodeListSo(codeListDo.getId(), codeListDo.getCode(), codeListDo.getName());
    }

    private CodeListItemSo toCodeListItemSo(final CodeListItemDo codeListCodeDo) {
        return new CodeListItemSo(
                codeListCodeDo.getId(),
                codeListCodeDo.getCodeListId(),
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
                .orElse(codeListItemRepository.countByCodeListIdAndParentIdNull(id));
    }

    private void sortItems(final Long id, final Long parentItemId) {
        final List<CodeListItemDo> items = getItems(id, parentItemId);
        for (final CodeListItemDo item : items) {
            item.setSortNum(items.indexOf(item));
        }
        codeListItemRepository.saveAll(items);
    }

    private List<CodeListItemDo> getItems(final Long id, final Long parentItemId) {
        final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.ASC, "sortNum");
        final CodeListItemSearchCriteriaSo criteria = Optional.ofNullable(parentItemId)
                .map(parentCategoryId -> CodeListItemSearchCriteriaSo.builder()
                        .codeListId(id)
                        .parentId(parentCategoryId)
                        .build())
                .orElse(CodeListItemSearchCriteriaSo.builder()
                        .codeListId(id)
                        .root(true)
                        .build());
        return codeListItemRepository.findAll(criteria, pageable).stream()
                .toList();
    }
}
