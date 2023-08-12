package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.model.product.ProductCategoryDataSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySearchCriteriaSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.ProductCategoryDo;
import sk.janobono.wiwa.dal.model.ProductCategorySearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.ProductCategoryRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductCategoryService {

    private final ScDf scDf;
    private final ProductCategoryRepository productCategoryRepository;

    public Page<ProductCategorySo> getProductCategories(final ProductCategorySearchCriteriaSo criteria, final Pageable pageable) {
        final Pageable categoriesPageable;
        if(pageable.getSort().isEmpty()){
            categoriesPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "sortNum");
        }else{
            categoriesPageable = pageable;
        }
        return productCategoryRepository.findAll(new ProductCategorySearchCriteriaDo(scDf, criteria), categoriesPageable)
                .map(this::toProductCategorySo);
    }

    public ProductCategorySo getProductCategory(final Long id) {
        return toProductCategorySo(getProductCategoryDo(id));
    }

    @Transactional
    public ProductCategorySo addProductCategory(final ProductCategoryDataSo productCategoryData) {
        if (isCodeUsed(null, productCategoryData.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product category code {0} is used", productCategoryData.code());
        }
        final Optional<ProductCategoryDo> parentCategory = getParentCategory(productCategoryData.parentId());

        final ProductCategoryDo productCategoryDo = new ProductCategoryDo();
        productCategoryDo.setParentId(productCategoryData.parentId());
        productCategoryDo.setCode(productCategoryData.code());
        productCategoryDo.setName(productCategoryData.name());
        productCategoryDo.setTreeCode(getTreeCode(parentCategory, productCategoryData.code()));
        productCategoryDo.setSortNum(getNextSortNum(parentCategory));

        return toProductCategorySo(productCategoryRepository.save(productCategoryDo));
    }

    @Transactional
    public ProductCategorySo setProductCategory(final Long id, final ProductCategoryDataSo productCategoryData) {
        ProductCategoryDo productCategoryDo = getProductCategoryDo(id);
        final Optional<ProductCategoryDo> parentCategory = getParentCategory(productCategoryData.parentId());
        if (isCodeUsed(id, productCategoryData.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product category code {0} is used", productCategoryData.code());
        }

        final Long previousParentId = productCategoryDo.getParentId();
        final boolean parentChanged = !Optional.ofNullable(previousParentId).orElse(-1L).equals(productCategoryData.parentId());

        productCategoryDo.setParentId(productCategoryData.parentId());
        productCategoryDo.setCode(productCategoryData.code());
        productCategoryDo.setName(productCategoryData.name());
        if (parentChanged) {
            productCategoryDo.setTreeCode(getTreeCode(parentCategory, productCategoryData.code()));
            productCategoryDo.setSortNum(getNextSortNum(parentCategory));
        }
        productCategoryDo = productCategoryRepository.save(productCategoryDo);

        if (parentChanged) {
            sortCategory(previousParentId);
        }

        return toProductCategorySo(productCategoryDo);
    }

    @Transactional
    public void deleteProductCategory(final Long id) {
        final ProductCategoryDo productCategoryDo = getProductCategoryDo(id);
        if (!isLeafNode(id)) {
            throw WiwaException.PRODUCT_CATEGORY_NOT_EMPTY.exception("Product category with id {0} not empty", id);
        }
        productCategoryRepository.deleteById(id);
        sortCategory(productCategoryDo.getParentId());
    }

    @Transactional
    public ProductCategorySo moveProductCategoryUp(final Long id) {
        final ProductCategoryDo productCategoryDo = getProductCategoryDo(id);
        sortCategory(productCategoryDo.getParentId());

        final List<ProductCategoryDo> categories = getChildren(productCategoryDo.getParentId());
        final int categoryIndex = categories.indexOf(productCategoryDo);
        if (categoryIndex > 0) {
            final List<ProductCategoryDo> batch = new ArrayList<>();

            final ProductCategoryDo upItem = categories.get(categoryIndex);
            upItem.setSortNum(upItem.getSortNum() - 1);
            batch.add(upItem);

            final ProductCategoryDo downItem = categories.get(categoryIndex - 1);
            downItem.setSortNum(downItem.getSortNum() + 1);
            batch.add(downItem);

            productCategoryRepository.saveAll(batch);
        }

        return toProductCategorySo(getProductCategoryDo(id));
    }

    @Transactional
    public ProductCategorySo moveProductCategoryDown(final Long id) {
        final ProductCategoryDo productCategoryDo = getProductCategoryDo(id);
        sortCategory(productCategoryDo.getParentId());

        final List<ProductCategoryDo> categories = getChildren(productCategoryDo.getParentId());
        final int categoryIndex = categories.indexOf(productCategoryDo);
        if (categoryIndex < categories.size() - 1) {
            final List<ProductCategoryDo> batch = new ArrayList<>();

            final ProductCategoryDo downItem = categories.get(categoryIndex);
            downItem.setSortNum(downItem.getSortNum() + 1);
            batch.add(downItem);

            final ProductCategoryDo upItem = categories.get(categoryIndex + 1);
            upItem.setSortNum(downItem.getSortNum() - 1);
            batch.add(upItem);

            productCategoryRepository.saveAll(batch);
        }

        return toProductCategorySo(getProductCategoryDo(id));
    }

    private ProductCategorySo toProductCategorySo(final ProductCategoryDo productCategory) {
        return new ProductCategorySo(
                productCategory.getId(),
                productCategory.getCode(),
                productCategory.getName(),
                isLeafNode(productCategory.getId())
        );
    }

    private boolean isCodeUsed(final Long id, final String code) {
        return Optional.ofNullable(id)
                .map(categoryId -> productCategoryRepository.countByIdNotAndCode(categoryId, code) > 0)
                .orElse(productCategoryRepository.countByCode(code) > 0);
    }

    private ProductCategoryDo getProductCategoryDo(final Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> WiwaException.PRODUCT_CATEGORY_NOT_FOUND.exception("Product category with id {0} not found", id));
    }

    private Optional<ProductCategoryDo> getParentCategory(final Long parentId) {
        final Optional<ProductCategoryDo> parentCategory;
        if (Optional.ofNullable(parentId).isPresent()) {
            final ProductCategoryDo productCategoryDo = productCategoryRepository.findById(parentId)
                    .orElseThrow(() -> WiwaException.PRODUCT_CATEGORY_NOT_FOUND.exception("Parent product category with id {0} not found", parentId));
            parentCategory = Optional.of(productCategoryDo);
        } else {
            parentCategory = Optional.empty();
        }
        return parentCategory;
    }

    private String getTreeCode(final Optional<ProductCategoryDo> parentCategory, final String code) {
        return parentCategory
                .map(category -> category.getTreeCode() + "::" + code)
                .orElse(code);
    }

    private Integer getNextSortNum(final Optional<ProductCategoryDo> parentCategory) {
        return parentCategory
                .map(category -> productCategoryRepository.countByParentId(category.getId()))
                .orElse(productCategoryRepository.countByParentIdNull());
    }

    private boolean isLeafNode(final Long id) {
        return productCategoryRepository.countByParentId(id) == 0;
    }

    private void sortCategory(final Long parentId) {
        final List<ProductCategoryDo> categories = getChildren(parentId);
        for (final ProductCategoryDo productCategoryDo : categories) {
            productCategoryDo.setSortNum(categories.indexOf(productCategoryDo));
        }
        productCategoryRepository.saveAll(categories);
    }

    private List<ProductCategoryDo> getChildren(final Long parentId) {
        final Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.ASC, "sortNum");
        final ProductCategorySearchCriteriaSo productCategorySearchCriteriaSo = Optional.ofNullable(parentId)
                .map(parentCategoryId -> ProductCategorySearchCriteriaSo.builder().parentCategoryId(parentCategoryId).build())
                .orElse(ProductCategorySearchCriteriaSo.builder().rootCategories(true).build());
        return productCategoryRepository.findAll(new ProductCategorySearchCriteriaDo(scDf, productCategorySearchCriteriaSo), pageable).stream()
                .toList();
    }
}
