package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.product.EdgeProductWebDto;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemChangeData;
import sk.janobono.wiwa.business.model.product.ProductChangeData;
import sk.janobono.wiwa.business.model.product.ProductQuantityData;
import sk.janobono.wiwa.business.model.product.ProductUnitPriceChangeData;
import sk.janobono.wiwa.business.service.ProductConfigService;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EdgeProductControllerTest extends BaseControllerTest {

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Autowired
    public ProductConfigService productConfigService;

    @Autowired
    public ProductService productService;

    @Autowired
    public PriceUtil priceUtil;

    @Test
    void fullTest() {
        final CodeListDo edgeCategory = codeListRepository.save(CodeListDo.builder()
                .code("EDGE")
                .name("edge")
                .build());

        final CodeListItemDo edgeCategoryItem01 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(edgeCategory.getId())
                .treeCode("E01")
                .code("E01")
                .value("edge item 01")
                .sortNum(1)
                .build());
        final CodeListItemDo edgeCategoryItem02 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(edgeCategory.getId())
                .treeCode("E02")
                .code("E02")
                .value("edge item 02")
                .sortNum(2)
                .build());

        productConfigService.setEdgeCategoryItem(new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem01.getId()));
        productConfigService.setEdgeSearchItems(List.of(
                new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem01.getId()),
                new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem02.getId())
        ));

        for (int i = 0; i < 10; i++) {
            final var product = productService.addProduct(new ProductChangeData(
                    "E" + i,
                    "Edge" + i,
                    "Edge product" + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(),
                    List.of(
                            new ProductQuantityData(ProductQuantityKey.SALE, BigDecimal.valueOf(100 + i), Unit.PIECE),
                            new ProductQuantityData(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(100 + i), Unit.KILOGRAM),
                            new ProductQuantityData(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(100 + i), Unit.KILOGRAM),
                            new ProductQuantityData(ProductQuantityKey.WIDTH, BigDecimal.valueOf(100 + i), Unit.MILLIMETER),
                            new ProductQuantityData(ProductQuantityKey.THICKNESS, BigDecimal.valueOf(100 + i), Unit.MILLIMETER)
                    )
            ));
            productService.setProductUnitPrices(product.id(), List.of(
                    new ProductUnitPriceChangeData(LocalDate.now(), BigDecimal.valueOf(100 + i), Unit.EUR)
            ));
            productService.setProductCategoryItems(product.id(), List.of(
                    new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem01.getId()),
                    new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem02.getId())
            ));
        }

        for (int i = 10; i < 20; i++) {
            final var product = productService.addProduct(new ProductChangeData(
                    "E" + i,
                    "Edge" + i,
                    "Edge product" + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(),
                    List.of()
            ));
            productService.setProductCategoryItems(product.id(), List.of(
                    new ProductCategoryItemChangeData(edgeCategory.getId(), edgeCategoryItem01.getId())
            ));
        }

        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        assertProduct(getEdgeProduct(headers, 1L), 0);
        assertProduct(getEdgeProduct(headers, 2L), 1);

        var page = getEdgeProducts(headers,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(10);

        page = getEdgeProducts(headers,
                "0", null, null, null, null, null, null, null,
                null, null, null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getEdgeProducts(headers,
                null, "E1", null, null, null, null, null, null,
                null, null, null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getEdgeProducts(headers,
                null, null, "Edge2", null, null, null, null, null,
                null, null, null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getEdgeProducts(headers,
                null, null, null, ProductStockStatus.TO_ORDER, null, null, null, null,
                null, null, null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(0);

        page = getEdgeProducts(headers,
                null, null, null, null, BigDecimal.valueOf(105), BigDecimal.valueOf(110), Unit.MILLIMETER, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getEdgeProducts(headers,
                null, null, null, null, null, null, null, BigDecimal.valueOf(105), BigDecimal.valueOf(110), Unit.MILLIMETER,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getEdgeProducts(headers,
                null, null, null, null, null,
                null, null, null, null, null,
                BigDecimal.valueOf(120), BigDecimal.valueOf(125), Unit.EUR, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getEdgeProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null,
                List.of(edgeCategoryItem01.getCode()), Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(10);

        page = getEdgeProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null,
                List.of(edgeCategoryItem02.getCode()), Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(0);

    }

    private void assertProduct(final EdgeProductWebDto edge, final int index) {
        final BigDecimal value = BigDecimal.valueOf(100 + index).setScale(3, RoundingMode.HALF_DOWN);
        assertThat(edge).isNotNull();
        assertThat(edge.id()).isEqualTo(index + 1);
        assertThat(edge.code()).isEqualTo("E" + index);
        assertThat(edge.name()).isEqualTo("Edge" + index);
        assertThat(edge.description()).isEqualTo("Edge product" + index);
        assertThat(edge.stockStatus()).isEqualTo(ProductStockStatus.ON_STOCK);
        assertThat(edge.saleValue()).isEqualTo(value);
        assertThat(edge.saleUnit()).isEqualTo(Unit.PIECE);
        assertThat(edge.weightValue()).isEqualTo(value);
        assertThat(edge.weightUnit()).isEqualTo(Unit.KILOGRAM);
        assertThat(edge.netWeightValue()).isEqualTo(value);
        assertThat(edge.netWeightUnit()).isEqualTo(Unit.KILOGRAM);
        assertThat(edge.widthValue()).isEqualTo(value);
        assertThat(edge.widthUnit()).isEqualTo(Unit.MILLIMETER);
        assertThat(edge.thicknessValue()).isEqualTo(value);
        assertThat(edge.thicknessUnit()).isEqualTo(Unit.MILLIMETER);
        assertThat(edge.priceValue()).isEqualTo(value);
        assertThat(edge.vatPriceValue()).isEqualTo(priceUtil.countVatValue(value, BigDecimal.valueOf(20)));
        assertThat(edge.priceUnit()).isEqualTo(Unit.EUR);
    }

    private EdgeProductWebDto getEdgeProduct(final HttpHeaders headers, final Long id) {
        return getEntity(EdgeProductWebDto.class, headers, "/edge-products", id);
    }

    private Page<EdgeProductWebDto> getEdgeProducts(final HttpHeaders headers,
                                                    final String searchField,
                                                    final String code,
                                                    final String name,
                                                    final ProductStockStatus stockStatus,
                                                    final BigDecimal widthFrom,
                                                    final BigDecimal widthTo,
                                                    final Unit widthUnit,
                                                    final BigDecimal thicknessFrom,
                                                    final BigDecimal thicknessTo,
                                                    final Unit thicknessUnit,
                                                    final BigDecimal priceFrom,
                                                    final BigDecimal priceTo,
                                                    final Unit priceUnit,
                                                    final List<String> codeListItems,
                                                    final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "searchField", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        Optional.ofNullable(stockStatus).ifPresent(v -> addToParams(params, "stockStatus", v.name()));
        Optional.ofNullable(widthFrom).ifPresent(v -> addToParams(params, "widthFrom", v.toPlainString()));
        Optional.ofNullable(widthTo).ifPresent(v -> addToParams(params, "widthTo", v.toPlainString()));
        Optional.ofNullable(widthUnit).ifPresent(v -> addToParams(params, "widthUnit", v.name()));
        Optional.ofNullable(thicknessFrom).ifPresent(v -> addToParams(params, "thicknessFrom", v.toPlainString()));
        Optional.ofNullable(thicknessTo).ifPresent(v -> addToParams(params, "thicknessTo", v.toPlainString()));
        Optional.ofNullable(thicknessUnit).ifPresent(v -> addToParams(params, "thicknessUnit", v.name()));
        Optional.ofNullable(priceFrom).ifPresent(v -> addToParams(params, "priceFrom", v.toPlainString()));
        Optional.ofNullable(priceTo).ifPresent(v -> addToParams(params, "priceTo", v.toPlainString()));
        Optional.ofNullable(priceUnit).ifPresent(v -> addToParams(params, "priceUnit", v.name()));
        Optional.ofNullable(codeListItems).ifPresent(l -> addToParams(params, "codeListItems", l));
        return getEntities(EdgeProductWebDto.class, headers, "/edge-products", params, pageable);
    }

}
