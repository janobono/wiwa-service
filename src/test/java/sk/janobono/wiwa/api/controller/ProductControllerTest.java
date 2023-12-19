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
import sk.janobono.wiwa.business.model.codelist.CodeListDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemDataSo;
import sk.janobono.wiwa.business.model.codelist.CodeListItemSo;
import sk.janobono.wiwa.business.model.codelist.CodeListSo;
import sk.janobono.wiwa.business.model.product.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.ProductAttributeKey;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProductControllerTest extends BaseControllerTest {
    @Autowired
    public CommonConfigProperties commonConfigProperties;

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<ProductSo> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            products.add(addProduct(headers, new ProductDataSo(
                    "code-undefined-" + i,
                    "name-undefined-" + i,
                    null,
                    ProductStockStatus.OUT_OF_STOCK,
                    null,
                    List.of(new ProductQuantityDataSo(ProductQuantityKey.SALE, new BigDecimal("0.000"), "PIECE"))
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    "code-board-" + i,
                    "name-board-" + i,
                    "this is board " + i,
                    ProductStockStatus.ON_STOCK,
                    List.of(
                            new ProductAttributeSo(ProductAttributeKey.BOARD_CODE, "BC" + i),
                            new ProductAttributeSo(ProductAttributeKey.STRUCTURE_CODE, "SC" + i),
                            new ProductAttributeSo(ProductAttributeKey.ORIENTATION, Boolean.toString(i % 2 == 0))
                    ),
                    List.of(
                            new ProductQuantityDataSo(ProductQuantityKey.SALE, new BigDecimal("1.000"), "PIECE"),
                            new ProductQuantityDataSo(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(i + 1).setScale(3, RoundingMode.HALF_UP), "KILOGRAM"),
                            new ProductQuantityDataSo(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(i + 1).subtract(new BigDecimal("0.5")).setScale(3, RoundingMode.HALF_UP), "KILOGRAM"),
                            new ProductQuantityDataSo(ProductQuantityKey.LENGTH, new BigDecimal("2800.000"), "MILLIMETER"),
                            new ProductQuantityDataSo(ProductQuantityKey.WIDTH, new BigDecimal("2070.000"), "MILLIMETER"),
                            new ProductQuantityDataSo(ProductQuantityKey.THICKNESS, new BigDecimal("18.000"), "MILLIMETER")
                    )
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    "code-edge-" + i,
                    "name-edge-" + i,
                    "this is edge " + i,
                    ProductStockStatus.ON_INQUIRE,
                    null,
                    List.of(
                            new ProductQuantityDataSo(ProductQuantityKey.SALE, new BigDecimal("1.000"), "MILLIMETER"),
                            new ProductQuantityDataSo(ProductQuantityKey.WEIGHT, new BigDecimal("1.000"), "GRAM"),
                            new ProductQuantityDataSo(ProductQuantityKey.NET_WEIGHT, new BigDecimal("0.900"), "GRAM"),
                            new ProductQuantityDataSo(ProductQuantityKey.WIDTH, i % 2 == 0 ? new BigDecimal("22.000") : new BigDecimal("44.000"), "MILLIMETER"),
                            new ProductQuantityDataSo(ProductQuantityKey.THICKNESS, new BigDecimal("2.500"), "MILLIMETER")
                    )
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    "code-service-" + i,
                    "name-service-" + i,
                    "this is service " + i,
                    ProductStockStatus.TO_ORDER,
                    null,
                    List.of(
                            new ProductQuantityDataSo(ProductQuantityKey.SALE, new BigDecimal("1.000"), "PIECE")
                    )
            )));
        }

        for (final ProductSo product : products) {
            assertThat(product)
                    .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                            .withIgnoredFields("attributes", "images", "quantities", "unitPrices", "codeListItems")
                            .build())
                    .isEqualTo(getProduct(headers, product.id()));
        }

        ProductSo testProduct = products.stream()
                .filter(p -> p.code().equals("code-board-0"))
                .findFirst()
                .orElseThrow();
        final Long testProductId = testProduct.id();
        final int productIndex = products.indexOf(testProduct);
        assertThat(productIndex).isNotEqualTo(-1);

        testProduct = setProduct(headers, testProduct.id(), new ProductDataSo(
                "SP01",
                "SPBC01",
                "This is test board",
                ProductStockStatus.ON_INQUIRE,
                List.of(
                        new ProductAttributeSo(ProductAttributeKey.BOARD_CODE, "SPSC01"),
                        new ProductAttributeSo(ProductAttributeKey.STRUCTURE_CODE, "Test board"),
                        new ProductAttributeSo(ProductAttributeKey.ORIENTATION, "false")
                ),
                List.of(
                        new ProductQuantityDataSo(ProductQuantityKey.SALE, new BigDecimal("1.000"), "PIECE"),
                        new ProductQuantityDataSo(ProductQuantityKey.WEIGHT, BigDecimal.valueOf(1).setScale(3, RoundingMode.HALF_UP), "KILOGRAM"),
                        new ProductQuantityDataSo(ProductQuantityKey.NET_WEIGHT, BigDecimal.valueOf(1).subtract(new BigDecimal("0.5")).setScale(3, RoundingMode.HALF_UP), "KILOGRAM"),
                        new ProductQuantityDataSo(ProductQuantityKey.LENGTH, new BigDecimal("2800.000"), "MILLIMETER"),
                        new ProductQuantityDataSo(ProductQuantityKey.WIDTH, new BigDecimal("2070.000"), "MILLIMETER"),
                        new ProductQuantityDataSo(ProductQuantityKey.THICKNESS, new BigDecimal("18.000"), "MILLIMETER")
                )
        ));
        products.set(productIndex, testProduct);

        for (final ProductSo product : products) {
            assertThat(product).usingRecursiveComparison().isEqualTo(getProduct(headers, product.id()));
        }

        setProductImage(token, testProductId, "test01.png");
        setProductImage(token, testProductId, "test02.png");
        final List<ApplicationImage> productImages = setProductImage(token, testProductId, "test03.png").images();
        final List<ApplicationImage> savedProductImages = getProduct(headers, testProductId).images();

        for (final ApplicationImage originalImage : productImages) {
            final ApplicationImage savedImage = savedProductImages.stream()
                    .filter(s -> s.fileName().equals(originalImage.fileName()))
                    .findFirst()
                    .orElseThrow();
            assertThat(savedImage).usingRecursiveComparison().isEqualTo(originalImage);
            assertThat(getProductImage(headers, testProductId, savedImage.fileName()))
                    .isEqualTo(imageUtil.scaleImage(
                            "png",
                            imageUtil.generateMessageImage(savedImage.fileName()),
                            commonConfigProperties.maxImageResolution(),
                            commonConfigProperties.maxImageResolution()
                    ));
        }

        List<ProductUnitPriceSo> productUnitPrices = setProductUnitPrices(headers, testProductId, List.of(
                new ProductUnitPriceDataSo(ZonedDateTime.now(), new BigDecimal("100.000"), "EUR")
        )).unitPrices();
        assertThat(productUnitPrices.get(0).value()).isEqualTo(new BigDecimal("100.000"));
        assertThat(productUnitPrices.get(0).unit()).isEqualTo("€");

        final CodeListSo codeListSo = addCodeList(headers, new CodeListDataSo("code", "test-code-list"));
        final CodeListItemSo codeListItem01 = addCodeListItem(headers, codeListSo.id(), new CodeListItemDataSo(null, "code1", "test-item1"));
        final CodeListItemSo codeListItem02 = addCodeListItem(headers, codeListSo.id(), new CodeListItemDataSo(null, "code2", "test-item2"));
        List<Long> codeListItems = setProductCodeListItems(headers, testProductId, List.of(codeListItem01.id())).codeListItems();
        List<Long> savedCodeListItems = getProduct(headers, testProductId).codeListItems();
        assertThat(codeListItems.size()).isEqualTo(1);
        assertThat(codeListItems.get(0)).isEqualTo(codeListItem01.id());
        assertThat(codeListItems.get(0)).isEqualTo(savedCodeListItems.get(0));

        codeListItems = setProductCodeListItems(headers, testProductId, List.of(codeListItem02.id())).codeListItems();
        savedCodeListItems = getProduct(headers, testProductId).codeListItems();
        assertThat(codeListItems.size()).isEqualTo(1);
        assertThat(codeListItems.get(0)).isEqualTo(codeListItem02.id());
        assertThat(codeListItems.get(0)).isEqualTo(savedCodeListItems.get(0));

        codeListItems = setProductCodeListItems(headers, testProductId, List.of(codeListItem01.id(), codeListItem02.id())).codeListItems();
        savedCodeListItems = getProduct(headers, testProductId).codeListItems();
        assertThat(codeListItems.size()).isEqualTo(2);
        assertThat(codeListItems.get(0)).isEqualTo(savedCodeListItems.get(0));
        assertThat(codeListItems.get(1)).isEqualTo(savedCodeListItems.get(1));

        Page<ProductSo> searchResult = getProducts(headers, "board-1", null, null, null, null, Pageable.unpaged());
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

        codeListItems = setProductCodeListItems(headers, testProductId, Collections.emptyList()).codeListItems();
        savedCodeListItems = getProduct(headers, testProductId).codeListItems();
        assertThat(codeListItems.size()).isEqualTo(0);
        assertThat(codeListItems.size()).isEqualTo(savedCodeListItems.size());

        for (final ApplicationImage originalImage : productImages) {
            deleteProductImage(headers, testProductId, originalImage.fileName());
        }
        for (final ProductSo product : products) {
            deleteProduct(headers, product.id());
        }
    }

    private ProductSo getProduct(final HttpHeaders headers, final Long id) {
        return getEntity(ProductSo.class, headers, "/products", id);
    }

    private Page<ProductSo> getProducts(final HttpHeaders headers,
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
        return getEntities(ProductSo.class, headers, "/products", params, pageable);
    }

    private ProductSo addProduct(final HttpHeaders headers, final ProductDataSo productData) {
        return addEntity(ProductSo.class, headers, "/products", productData);
    }

    private ProductSo setProduct(final HttpHeaders headers, final Long id, final ProductDataSo productData) {
        return setEntity(ProductSo.class, headers, "/products", id, productData);
    }

    private void deleteProduct(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/products", id);
    }

    private byte[] getProductImage(final HttpHeaders headers, final Long id, final String fileName) {
        final ResponseEntity<byte[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-images/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                HttpMethod.GET, new HttpEntity<>(headers),
                byte[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private ProductSo setProductImage(final String token, final Long id, final String fileName) {
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

        final ResponseEntity<ProductSo> response = restTemplate.exchange(
                getURI("/products/{id}/product-images", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                httpEntity,
                ProductSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductSo deleteProductImage(final HttpHeaders headers, final Long id, final String fileName) {
        final ResponseEntity<ProductSo> response = restTemplate.exchange(
                getURI("/products/{id}/product-images/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                ProductSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductSo setProductUnitPrices(final HttpHeaders headers, final Long id, final List<ProductUnitPriceDataSo> productUnitPrices) {
        final ResponseEntity<ProductSo> response = restTemplate.exchange(
                getURI("/products/{id}/product-unit-prices", Map.of("id", id.toString())),
                HttpMethod.POST,
                new HttpEntity<>(productUnitPrices, headers),
                ProductSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public ProductSo setProductCodeListItems(final HttpHeaders headers, final Long id, final List<Long> itemIds) {
        final ResponseEntity<ProductSo> response = restTemplate.exchange(
                getURI("/products/{id}/code-list-items", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                new HttpEntity<>(itemIds, headers),
                ProductSo.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }
}
