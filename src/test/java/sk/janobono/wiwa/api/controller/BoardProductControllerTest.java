package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.product.BoardProductWebDto;
import sk.janobono.wiwa.business.model.product.*;
import sk.janobono.wiwa.business.service.ProductConfigService;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.ProductAttributeKey;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BoardProductControllerTest extends BaseControllerTest {

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
        final CodeListDo boardCategory = codeListRepository.save(CodeListDo.builder()
                .code("BOARD")
                .name("board")
                .build());

        final CodeListItemDo boardCategoryItem01 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(boardCategory.getId())
                .treeCode("B01")
                .code("B01")
                .value("board item 01")
                .sortNum(1)
                .build());
        final CodeListItemDo boardCategoryItem02 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(boardCategory.getId())
                .treeCode("B02")
                .code("B02")
                .value("board item 02")
                .sortNum(2)
                .build());

        productConfigService.setBoardCategoryItem(new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem01.getId()));
        productConfigService.setBoardSearchItems(List.of(
                new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem01.getId()),
                new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem02.getId())
        ));

        for (int i = 0; i < 10; i++) {
            final var product = productService.addProduct(new ProductChangeData(
                    "B" + i,
                    "Board" + i,
                    "Board product" + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(
                            new ProductAttributeData(ProductAttributeKey.STRUCTURE_CODE, "SC" + i),
                            new ProductAttributeData(ProductAttributeKey.BOARD_CODE, "BC" + i),
                            new ProductAttributeData(ProductAttributeKey.ORIENTATION, Boolean.valueOf(i % 2 == 0).toString())
                    ),
                    List.of(
                            new ProductQuantityData(ProductQuantityKey.SALE, BigDecimal.valueOf(100 + i), Unit.PIECE),
                            new ProductQuantityData(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(100 + i), Unit.KILOGRAM),
                            new ProductQuantityData(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(100 + i), Unit.KILOGRAM),
                            new ProductQuantityData(ProductQuantityKey.LENGTH, BigDecimal.valueOf(100 + i), Unit.MILLIMETER),
                            new ProductQuantityData(ProductQuantityKey.WIDTH, BigDecimal.valueOf(100 + i), Unit.MILLIMETER),
                            new ProductQuantityData(ProductQuantityKey.THICKNESS, BigDecimal.valueOf(100 + i), Unit.MILLIMETER)
                    )
            ));
            productService.setProductUnitPrices(product.id(), List.of(
                    new ProductUnitPriceChangeData(LocalDate.now(), BigDecimal.valueOf(100 + i), Unit.EUR)
            ));
            productService.setProductCategoryItems(product.id(), List.of(
                    new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem01.getId()),
                    new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem02.getId())
            ));
        }

        for (int i = 10; i < 20; i++) {
            final var product = productService.addProduct(new ProductChangeData(
                    "B" + i,
                    "Board" + i,
                    "Board product" + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(),
                    List.of()
            ));
            productService.setProductCategoryItems(product.id(), List.of(
                    new ProductCategoryItemChangeData(boardCategory.getId(), boardCategoryItem01.getId())
            ));
        }

        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        assertProduct(getBoardProduct(headers, 1L), 0);
        assertProduct(getBoardProduct(headers, 2L), 1);

        var page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(10);

        page = getBoardProducts(headers,
                "0", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getBoardProducts(headers,
                null, "B1", null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getBoardProducts(headers,
                null, null, "Board2", null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getBoardProducts(headers,
                null, null, null, ProductStockStatus.TO_ORDER, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(0);

        page = getBoardProducts(headers,
                null, null, null, null, "BC3", null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getBoardProducts(headers,
                null, null, null, null, null, "SC4", null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(1);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, true, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, BigDecimal.valueOf(105),
                BigDecimal.valueOf(110), Unit.MILLIMETER, null, null, null, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, BigDecimal.valueOf(105), BigDecimal.valueOf(110), Unit.MILLIMETER, null, null, null,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, BigDecimal.valueOf(105), BigDecimal.valueOf(110), Unit.MILLIMETER,
                null, null, null, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                BigDecimal.valueOf(120), BigDecimal.valueOf(125), Unit.EUR, null, Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(5);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, List.of(boardCategoryItem01.getCode()), Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(10);

        page = getBoardProducts(headers,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, List.of(boardCategoryItem02.getCode()), Pageable.unpaged());
        assertThat(page.getSize()).isEqualTo(0);
    }

    private void assertProduct(final BoardProductWebDto board, final int index) {
        final BigDecimal value = BigDecimal.valueOf(100 + index).setScale(3, RoundingMode.HALF_DOWN);
        assertThat(board).isNotNull();
        assertThat(board.id()).isEqualTo(index + 1);
        assertThat(board.code()).isEqualTo("B" + index);
        assertThat(board.name()).isEqualTo("Board" + index);
        assertThat(board.description()).isEqualTo("Board product" + index);
        assertThat(board.stockStatus()).isEqualTo(ProductStockStatus.ON_STOCK);
        assertThat(board.boardCode()).isEqualTo("BC" + index);
        assertThat(board.structureCode()).isEqualTo("SC" + index);
        assertThat(board.orientation()).isEqualTo(index % 2 == 0);
        assertThat(board.saleValue()).isEqualTo(value);
        assertThat(board.saleUnit()).isEqualTo(Unit.PIECE);
        assertThat(board.weightValue()).isEqualTo(value);
        assertThat(board.weightUnit()).isEqualTo(Unit.KILOGRAM);
        assertThat(board.netWeightValue()).isEqualTo(value);
        assertThat(board.netWeightUnit()).isEqualTo(Unit.KILOGRAM);
        assertThat(board.lengthValue()).isEqualTo(value);
        assertThat(board.lengthUnit()).isEqualTo(Unit.MILLIMETER);
        assertThat(board.widthValue()).isEqualTo(value);
        assertThat(board.widthUnit()).isEqualTo(Unit.MILLIMETER);
        assertThat(board.thicknessValue()).isEqualTo(value);
        assertThat(board.thicknessUnit()).isEqualTo(Unit.MILLIMETER);
        assertThat(board.priceValue()).isEqualTo(value);
        assertThat(board.vatPriceValue()).isEqualTo(priceUtil.countVatValue(value, BigDecimal.valueOf(20)));
        assertThat(board.priceUnit()).isEqualTo(Unit.EUR);
    }

    private BoardProductWebDto getBoardProduct(final HttpHeaders headers, final Long id) {
        return getEntity(BoardProductWebDto.class, headers, "/board-products", id);
    }

    private Page<BoardProductWebDto> getBoardProducts(final HttpHeaders headers,
                                                      final String searchField,
                                                      final String code,
                                                      final String name,
                                                      final ProductStockStatus stockStatus,
                                                      final String boardCode,
                                                      final String structureCode,
                                                      final Boolean orientation,
                                                      final BigDecimal lengthFrom,
                                                      final BigDecimal lengthTo,
                                                      final Unit lengthUnit,
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
        Optional.ofNullable(boardCode).ifPresent(v -> addToParams(params, "boardCode", v));
        Optional.ofNullable(structureCode).ifPresent(v -> addToParams(params, "structureCode", v));
        Optional.ofNullable(orientation).ifPresent(v -> addToParams(params, "orientation", v.toString()));
        Optional.ofNullable(lengthFrom).ifPresent(v -> addToParams(params, "lengthFrom", v.toPlainString()));
        Optional.ofNullable(lengthTo).ifPresent(v -> addToParams(params, "lengthTo", v.toPlainString()));
        Optional.ofNullable(lengthUnit).ifPresent(v -> addToParams(params, "lengthUnit", v.name()));
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
        return getEntities(BoardProductWebDto.class, headers, "/board-products", params, pageable);
    }
}
