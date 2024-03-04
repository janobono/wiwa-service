package sk.janobono.wiwa.api.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.product.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProductControllerTest extends BaseControllerTest {

    @Autowired
    public CommonConfigProperties commonConfigProperties;

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<ProductWebDto> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.add(addProduct(headers, new ProductChangeWebDto(
                    "code-undefined-" + i,
                    "name-undefined-" + i,
                    null,
                    ProductStockStatus.OUT_OF_STOCK,
                    null,
                    List.of(new ProductQuantityWebDto(ProductQuantityKey.SALE, new BigDecimal("0.000"), Unit.PACKAGE))
            )));

            products.add(addProduct(headers, new ProductChangeWebDto(
                    "code-board-" + i,
                    "name-board-" + i,
                    "this is board " + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(
                            new ProductAttributeWebDto(ProductAttributeKey.BOARD_CODE, "BC" + i),
                            new ProductAttributeWebDto(ProductAttributeKey.STRUCTURE_CODE, "SC" + i),
                            new ProductAttributeWebDto(ProductAttributeKey.ORIENTATION, Boolean.toString(i % 2 == 0))
                    ),
                    List.of(
                            new ProductQuantityWebDto(ProductQuantityKey.SALE, new BigDecimal("1.000"), Unit.PIECE),
                            new ProductQuantityWebDto(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(i + 1).setScale(3, RoundingMode.HALF_UP), Unit.KILOGRAM),
                            new ProductQuantityWebDto(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(i + 1).subtract(new BigDecimal("0.5")).setScale(3, RoundingMode.HALF_UP), Unit.KILOGRAM),
                            new ProductQuantityWebDto(ProductQuantityKey.LENGTH, new BigDecimal("2800.000"), Unit.MILLILITER),
                            new ProductQuantityWebDto(ProductQuantityKey.WIDTH, new BigDecimal("2070.000"), Unit.MILLILITER),
                            new ProductQuantityWebDto(ProductQuantityKey.THICKNESS, new BigDecimal("18.000"), Unit.MILLILITER)
                    )
            )));

            products.add(addProduct(headers, new ProductChangeWebDto(
                    "code-edge-" + i,
                    "name-edge-" + i,
                    "this is edge " + i,
                    ProductStockStatus.ON_INQUIRE,
                    null,
                    List.of(
                            new ProductQuantityWebDto(ProductQuantityKey.SALE, new BigDecimal("1.000"), Unit.PIECE),
                            new ProductQuantityWebDto(ProductQuantityKey.WEIGHT, new BigDecimal("1.000"), Unit.KILOGRAM),
                            new ProductQuantityWebDto(ProductQuantityKey.NET_WEIGHT, new BigDecimal("0.900"), Unit.KILOGRAM),
                            new ProductQuantityWebDto(ProductQuantityKey.WIDTH, i % 2 == 0 ? new BigDecimal("22.000") : new BigDecimal("44.000"), Unit.MILLILITER),
                            new ProductQuantityWebDto(ProductQuantityKey.THICKNESS, new BigDecimal("2.500"), Unit.MILLILITER)
                    )
            )));

            products.add(addProduct(headers, new ProductChangeWebDto(
                    "code-service-" + i,
                    "name-service-" + i,
                    "this is service " + i,
                    ProductStockStatus.TO_ORDER,
                    null,
                    List.of(
                            new ProductQuantityWebDto(ProductQuantityKey.SALE, new BigDecimal("1.000"), Unit.PIECE)
                    )
            )));
        }

        for (final ProductWebDto product : products) {
            assertThat(product)
                    .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                            .withIgnoredFields("attributes", "images", "quantities", "unitPrices", "categoryItems")
                            .build())
                    .isEqualTo(getProduct(headers, product.id()));
        }

        ProductWebDto testProduct = products.stream()
                .filter(p -> p.code().equals("code-board-0"))
                .findFirst()
                .orElseThrow();
        final Long testProductId = testProduct.id();
        final int productIndex = products.indexOf(testProduct);
        assertThat(productIndex).isNotEqualTo(-1);

        testProduct = setProduct(headers, testProduct.id(), new ProductChangeWebDto(
                "SP01",
                "SPBC01",
                "This is test board",
                ProductStockStatus.ON_INQUIRE,
                List.of(
                        new ProductAttributeWebDto(ProductAttributeKey.BOARD_CODE, "SPSC01"),
                        new ProductAttributeWebDto(ProductAttributeKey.STRUCTURE_CODE, "Test board"),
                        new ProductAttributeWebDto(ProductAttributeKey.ORIENTATION, "false")
                ),
                List.of(
                        new ProductQuantityWebDto(ProductQuantityKey.SALE, new BigDecimal("1.000"), Unit.PIECE),
                        new ProductQuantityWebDto(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(1).setScale(3, RoundingMode.HALF_UP), Unit.KILOGRAM),
                        new ProductQuantityWebDto(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(1).subtract(new BigDecimal("0.5")).setScale(3, RoundingMode.HALF_UP), Unit.KILOGRAM),
                        new ProductQuantityWebDto(ProductQuantityKey.LENGTH, new BigDecimal("2800.000"), Unit.MILLILITER),
                        new ProductQuantityWebDto(ProductQuantityKey.WIDTH, new BigDecimal("2070.000"), Unit.MILLILITER),
                        new ProductQuantityWebDto(ProductQuantityKey.THICKNESS, new BigDecimal("18.000"), Unit.MILLILITER)
                )
        ));
        products.set(productIndex, testProduct);

        for (final ProductWebDto product : products) {
            assertThat(product).usingRecursiveComparison().isEqualTo(getProduct(headers, product.id()));
        }

        setProductImage(token, testProductId, "test01.png");
        setProductImage(token, testProductId, "test02.png");
        final List<ApplicationImageInfoWebDto> productImages = setProductImage(token, testProductId, "test03.png").images();
        final List<ApplicationImageInfoWebDto> savedProductImages = getProduct(headers, testProductId).images();

        for (final ApplicationImageInfoWebDto originalImage : productImages) {
            final ApplicationImageInfoWebDto savedImage = savedProductImages.stream()
                    .filter(s -> s.fileName().equals(originalImage.fileName()))
                    .findFirst()
                    .orElseThrow();
            assertThat(savedImage).usingRecursiveComparison().isEqualTo(originalImage);
            assertThat(getProductImage(testProductId, savedImage.fileName()))
                    .isEqualTo(imageUtil.scaleImage(
                            "png",
                            imageUtil.generateMessageImage(savedImage.fileName()),
                            commonConfigProperties.maxImageResolution(),
                            commonConfigProperties.maxImageResolution()
                    ));
        }

        List<ProductUnitPriceWebDto> productUnitPrices = setProductUnitPrices(headers, testProductId, List.of(
                new ProductUnitPriceChangeWebDto(LocalDate.now(), new BigDecimal("100.000"), Unit.EUR)
        )).unitPrices();
        assertThat(productUnitPrices.getFirst().value()).isEqualTo(new BigDecimal("100.000"));
        assertThat(productUnitPrices.getFirst().unit()).isEqualTo(Unit.EUR);

        final CodeListDo codeList = codeListRepository.save(CodeListDo.builder()
                .code("code")
                .name("test-code-list")
                .build());
        final CodeListItemDo codeListItem01 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code1")
                .code("code1")
                .value("test-item1")
                .sortNum(1)
                .build());
        final CodeListItemDo codeListItem02 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code2")
                .code("code2")
                .value("test-item2")
                .sortNum(2)
                .build());
        List<ProductCategoryItemWebDto> categoryItems = setProductCodeListItems(headers, testProductId,
                List.of(new ProductCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()))).categoryItems();
        List<ProductCategoryItemWebDto> savedCategoryItems = getProduct(headers, testProductId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem01.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setProductCodeListItems(headers, testProductId,
                List.of(new ProductCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getProduct(headers, testProductId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem02.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setProductCodeListItems(headers, testProductId,
                List.of(new ProductCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()),
                        new ProductCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getProduct(headers, testProductId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(2);
        assertThat(categoryItems.get(0).id()).isEqualTo(savedCategoryItems.get(0).id());
        assertThat(categoryItems.get(1)).isEqualTo(savedCategoryItems.get(1));

        Page<ProductWebDto> searchResult = getProducts(headers, "board-1", null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, "code-service-1", null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, "name-edge-1", null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, List.of("code1"), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, List.of("code2"), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, ProductStockStatus.ON_INQUIRE, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(11);

        productUnitPrices = setProductUnitPrices(headers, testProductId, Collections.emptyList()).unitPrices();
        assertThat(productUnitPrices.size()).isEqualTo(0);
        assertThat(productUnitPrices.size()).isEqualTo(getProduct(headers, testProductId).unitPrices().size());

        categoryItems = setProductCodeListItems(headers, testProductId, Collections.emptyList()).categoryItems();
        savedCategoryItems = getProduct(headers, testProductId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(0);
        assertThat(categoryItems.size()).isEqualTo(savedCategoryItems.size());

        for (final ApplicationImageInfoWebDto originalImage : productImages) {
            deleteProductImage(headers, testProductId, originalImage.fileName());
        }
        for (final ProductWebDto product : products) {
            deleteProduct(headers, product.id());
        }
    }

    private ProductWebDto getProduct(final HttpHeaders headers, final Long id) {
        return getEntity(ProductWebDto.class, headers, "/products", id);
    }

    private Page<ProductWebDto> getProducts(final HttpHeaders headers,
                                            final String searchField,
                                            final String code,
                                            final String name,
                                            final ProductStockStatus stockStatus,
                                            final List<String> codeListItems,
                                            final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "searchField", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        Optional.ofNullable(stockStatus).ifPresent(v -> addToParams(params, "stockStatus", v.name()));
        Optional.ofNullable(codeListItems).ifPresent(l -> addToParams(params, "codeListItems", l));
        return getEntities(ProductWebDto.class, headers, "/products", params, pageable);
    }

    private ProductWebDto addProduct(final HttpHeaders headers, final ProductChangeWebDto data) {
        return addEntity(ProductWebDto.class, headers, "/products", data);
    }

    private ProductWebDto setProduct(final HttpHeaders headers, final Long id, final ProductChangeWebDto data) {
        return setEntity(ProductWebDto.class, headers, "/products", id, data);
    }

    private void deleteProduct(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/products", id);
    }

    private byte[] getProductImage(final Long id, final String fileName) {
        return restTemplate.getForObject(
                getURI("/ui/product-images/{id}/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                byte[].class
        );
    }

    private ProductWebDto setProductImage(final String token, final Long id, final String fileName) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage(fileName)) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        final ResponseEntity<ProductWebDto> response = restTemplate.exchange(
                getURI("/products/{id}/images", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                httpEntity,
                ProductWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductWebDto deleteProductImage(final HttpHeaders headers, final Long id, final String fileName) {
        final ResponseEntity<ProductWebDto> response = restTemplate.exchange(
                getURI("/products/{id}/images/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                ProductWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductWebDto setProductUnitPrices(final HttpHeaders headers, final Long id, final List<ProductUnitPriceChangeWebDto> productUnitPrices) {
        final ResponseEntity<ProductWebDto> response = restTemplate.exchange(
                getURI("/products/{id}/unit-prices", Map.of("id", id.toString())),
                HttpMethod.POST,
                new HttpEntity<>(productUnitPrices, headers),
                ProductWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductWebDto setProductCodeListItems(final HttpHeaders headers, final Long id, final List<ProductCategoryItemChangeWebDto> categoryItems) {
        final ResponseEntity<ProductWebDto> response = restTemplate.exchange(
                getURI("/products/{id}/category-items", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                new HttpEntity<>(categoryItems, headers),
                ProductWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }
}
