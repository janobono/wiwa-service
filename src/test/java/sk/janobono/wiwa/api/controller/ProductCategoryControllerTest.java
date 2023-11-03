package sk.janobono.wiwa.api.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.business.model.product.ProductCategoryDataSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCategoryControllerTest extends BaseIntegrationTest {

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<ProductCategorySo> categories = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final ProductCategorySo parentCategory = addProductCategory(headers, new ProductCategoryDataSo(null, "code-" + i, "name-" + i));
            categories.add(parentCategory);
            for (int j = 0; j < 5; j++) {
                categories.add(addProductCategory(headers, new ProductCategoryDataSo(parentCategory.id(), "code-" + parentCategory.id() + "-" + j, "name-" + parentCategory.id() + "-" + j)));
            }
        }

        for (final ProductCategorySo productCategorySo : categories) {
            assertThat(productCategorySo).usingRecursiveComparison(RecursiveComparisonConfiguration.builder().withIgnoredFields("leafNode").build()).isEqualTo(getCategory(headers, productCategorySo.id()));
        }

        categories.clear();
        categories.addAll(getCategories(headers, null, null, null, null, null, null, Pageable.unpaged()).stream().toList());
        for (final ProductCategorySo productCategorySo : categories) {
            assertThat(productCategorySo).usingRecursiveComparison().isEqualTo(getCategory(headers, productCategorySo.id()));
        }

        final List<ProductCategorySo> searchedCategories = getCategories(headers, null, null, "name-1-", null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(searchedCategories.size()).isEqualTo(5);

        final List<ProductCategorySo> codeCategories = getCategories(headers, null, null, null, "code-1-1", null, null, Pageable.unpaged()).stream().toList();
        assertThat(codeCategories.size()).isEqualTo(1);

        final List<ProductCategorySo> nameCategories = getCategories(headers, null, null, null, "", "name-13-1", null, Pageable.unpaged()).stream().toList();
        assertThat(nameCategories.size()).isEqualTo(1);

        nameCategories.forEach(category -> {
            setProductCategory(headers, category.id(), new ProductCategoryDataSo(null, "code-x", "name-x"));
        });

        final List<ProductCategorySo> treeCategories = getCategories(headers, null, null, null, "", null, "code-1", Pageable.unpaged()).stream().toList();
        assertThat(treeCategories.size()).isEqualTo(6);


        List<ProductCategorySo> rootCategories = getCategories(headers, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(rootCategories.size()).isEqualTo(11);

        final ProductCategorySo movedCategory = rootCategories.get(0);
        moveProductCategoryDown(headers, movedCategory.id());
        rootCategories = getCategories(headers, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedCategory).usingRecursiveComparison().isEqualTo(rootCategories.get(1));
        moveProductCategoryUp(headers, movedCategory.id());
        rootCategories = getCategories(headers, true, null, null, null, null, null, Pageable.unpaged()).stream().toList();
        assertThat(movedCategory).usingRecursiveComparison().isEqualTo(rootCategories.get(0));

        rootCategories.forEach(rootCategory -> {
            final List<ProductCategorySo> children = getCategories(headers, null, rootCategory.id(), null, null, null, null, Pageable.unpaged()).stream().toList();
            children.forEach(child -> {
                assertThat(child.leafNode()).isTrue();
            });
        });

        rootCategories.forEach(rootCategory -> {
            final List<ProductCategorySo> children = getCategories(headers, null, rootCategory.id(), null, null, null, null, Pageable.unpaged()).stream().toList();
            children.forEach(child -> {
                deleteProductCategory(headers, child.id());
            });
            deleteProductCategory(headers, rootCategory.id());
        });
    }

    private ProductCategorySo getCategory(final HttpHeaders headers, final Long id) {
        return getEntity(ProductCategorySo.class, headers, "/product-categories", id);
    }

    private Page<ProductCategorySo> getCategories(final HttpHeaders headers, final Boolean rootCategories, final Long parentCategoryId, final String searchField, final String code, final String name, final String treeCode, final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(rootCategories).ifPresent(v -> addToParams(params, "root-categories", v.toString()));
        Optional.ofNullable(parentCategoryId).ifPresent(v -> addToParams(params, "parent-category-id", v.toString()));
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "search-field", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        Optional.ofNullable(treeCode).ifPresent(v -> addToParams(params, "tree-code", v));
        return getEntities(ProductCategorySo.class, headers, "/product-categories", params, pageable);
    }

    private ProductCategorySo addProductCategory(final HttpHeaders headers, final ProductCategoryDataSo productCategoryData) {
        return addEntity(ProductCategorySo.class, headers, "/product-categories", productCategoryData);
    }

    private ProductCategorySo setProductCategory(final HttpHeaders headers, final Long id, final ProductCategoryDataSo productCategoryData) {
        return setEntity(ProductCategorySo.class, headers, "/product-categories", id, productCategoryData);
    }

    private void deleteProductCategory(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/product-categories", id);
    }

    private void moveProductCategoryUp(final HttpHeaders headers, final Long id) {
        final ResponseEntity<ProductCategorySo> response = restTemplate.exchange(getURI("/product-categories/{id}/move-up", Map.of("id", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), ProductCategorySo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    private void moveProductCategoryDown(final HttpHeaders headers, final Long id) {
        final ResponseEntity<ProductCategorySo> response = restTemplate.exchange(getURI("/product-categories/{id}/move-down", Map.of("id", Long.toString(id))), HttpMethod.PATCH, new HttpEntity<>(headers), ProductCategorySo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}