package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.product.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;
import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProductControllerTest extends BaseIntegrationTest {

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
                    ProductType.UNDEFINED,
                    "code-undefined-" + i,
                    null,
                    null,
                    "name-undefined-" + i,
                    null,
                    new Quantity(new BigDecimal("0.000"), "p."),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ProductStockStatus.OUT_OF_STOCK
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    ProductType.BOARD,
                    "code-board-" + i,
                    "BC" + i,
                    "SC" + i,
                    "name-board-" + i,
                    "this is board " + i,
                    new Quantity(new BigDecimal("1.000"), "p."),
                    new Quantity(BigDecimal.valueOf(i + 1).setScale(3), "kg"),
                    new Quantity(BigDecimal.valueOf(i + 1).subtract(new BigDecimal("0.5")).setScale(3), "kg"),
                    new Quantity(new BigDecimal("2800.000"), "mm"),
                    new Quantity(new BigDecimal("2070.000"), "mm"),
                    new Quantity(new BigDecimal("18.000"), "mm"),
                    i % 2 == 0,
                    ProductStockStatus.ON_STOCK
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    ProductType.EDGE,
                    "code-edge-" + i,
                    null,
                    null,
                    "name-edge-" + i,
                    "this is edge " + i,
                    new Quantity(new BigDecimal("1.000"), "mm"),
                    new Quantity(new BigDecimal("1.000"), "kg/m"),
                    new Quantity(new BigDecimal("0.900"), "kg/m"),
                    null,
                    new Quantity(i % 2 == 0 ? new BigDecimal("22.000") : new BigDecimal("44.000"), "mm"),
                    new Quantity(new BigDecimal("2.500"), "mm"),
                    i % 2 == 0,
                    ProductStockStatus.ON_INQUIRE
            )));

            products.add(addProduct(headers, new ProductDataSo(
                    ProductType.SERVICE,
                    "code-service-" + i,
                    null,
                    null,
                    "name-service-" + i,
                    "this is service " + i,
                    new Quantity(new BigDecimal("1.000"), "p."),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ProductStockStatus.TO_ORDER
            )));
        }

        for (final ProductSo product : products) {
            assertThat(product).usingRecursiveComparison().isEqualTo(getProduct(headers, product.id()));
        }

        ProductSo testProduct = products.stream()
                .filter(p -> p.type() == ProductType.BOARD)
                .findFirst()
                .orElseThrow();
        final Long testProductId = testProduct.id();
        final int productIndex = products.indexOf(testProduct);
        assertThat(productIndex).isNotEqualTo(-1);

        testProduct = setProduct(headers, testProduct.id(), new ProductDataSo(
                ProductType.BOARD,
                "SP01",
                "SPBC01",
                "SPSC01",
                "Test board",
                "This is test board",
                testProduct.saleUnit(),
                testProduct.weight(),
                testProduct.netWeight(),
                testProduct.length(),
                testProduct.width(),
                testProduct.thickness(),
                testProduct.orientation(),
                ProductStockStatus.ON_INQUIRE
        ));
        products.set(productIndex, testProduct);

        for (final ProductSo product : products) {
            assertThat(product).usingRecursiveComparison().isEqualTo(getProduct(headers, product.id()));
        }

        final List<ApplicationImageWeb> productImages = Stream.of("test01.png", "test02.png", "test03.png")
                .map(fileName -> setProductImage(token, testProductId, fileName))
                .toList();

        final List<ApplicationImageWeb> savedProductImages = getProductImages(headers, testProductId);

        for (final ApplicationImageWeb originalImage : productImages) {
            final ApplicationImageWeb savedImage = savedProductImages.stream()
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
                new ProductUnitPriceSo(ZonedDateTime.now(), new Quantity(new BigDecimal("100.000"), "eur"))
        ));
        List<ProductUnitPriceSo> savedProductUnitPrices = getProductUnitPrices(headers, testProductId);
        assertThat(productUnitPrices.get(0)).usingRecursiveComparison().isEqualTo(savedProductUnitPrices.get(0));

        final ProductCategorySo productCategory01 = addEntity(ProductCategorySo.class, headers, "/product-categories", new ProductCategoryDataSo(
                null, "code1", "test-category1"
        ));
        final ProductCategorySo productCategory02 = addEntity(ProductCategorySo.class, headers, "/product-categories", new ProductCategoryDataSo(
                null, "code2", "test-category2"
        ));
        List<Long> productCategoryIds = setProductCategoryIds(headers, testProductId, List.of(productCategory01.id()));
        List<Long> savedProductCategoryIds = getProductCategoryIds(headers, testProductId);
        assertThat(productCategoryIds.size()).isEqualTo(1);
        assertThat(productCategoryIds.get(0)).isEqualTo(productCategory01.id());
        assertThat(productCategoryIds.get(0)).isEqualTo(savedProductCategoryIds.get(0));

        productCategoryIds = setProductCategoryIds(headers, testProductId, List.of(productCategory02.id()));
        savedProductCategoryIds = getProductCategoryIds(headers, testProductId);
        assertThat(productCategoryIds.size()).isEqualTo(1);
        assertThat(productCategoryIds.get(0)).isEqualTo(productCategory02.id());
        assertThat(productCategoryIds.get(0)).isEqualTo(savedProductCategoryIds.get(0));

        productCategoryIds = setProductCategoryIds(headers, testProductId, List.of(productCategory01.id(), productCategory02.id()));
        savedProductCategoryIds = getProductCategoryIds(headers, testProductId);
        assertThat(productCategoryIds.size()).isEqualTo(2);
        assertThat(productCategoryIds.get(0)).isEqualTo(savedProductCategoryIds.get(0));
        assertThat(productCategoryIds.get(1)).isEqualTo(savedProductCategoryIds.get(1));

        Page<ProductSo> searchResult = getProducts(headers, "board 1", null, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(2);

        searchResult = getProducts(headers, null, ProductType.BOARD, null, null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);

        searchResult = getProducts(headers, null, null, "code-service-1", null, null, null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, "name-edge-1", null, null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, "code1", null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, "code2", null, null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, "BC1", null, null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, null, "SC1", null, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, ProductStockStatus.ON_INQUIRE, null, null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(11);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, null, new BigDecimal("100"), null, null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, null, null, new BigDecimal("100"), null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, null, new BigDecimal("100"), new BigDecimal("100"), null, null, null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, null, null, null, new BigDecimal("18"), "mm", null, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);

        searchResult = getProducts(headers, null, null, null, null, null, null, null, null, null, null, null, null, true, Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(10);

        productUnitPrices = setProductUnitPrices(headers, testProductId, Collections.emptyList());
        savedProductUnitPrices = getProductUnitPrices(headers, testProductId);
        assertThat(productUnitPrices.size()).isEqualTo(0);
        assertThat(productUnitPrices.size()).isEqualTo(savedProductUnitPrices.size());

        productCategoryIds = setProductCategoryIds(headers, testProductId, Collections.emptyList());
        savedProductCategoryIds = getProductCategoryIds(headers, testProductId);
        assertThat(productCategoryIds.size()).isEqualTo(0);
        assertThat(productCategoryIds.size()).isEqualTo(savedProductCategoryIds.size());

        for (final ApplicationImageWeb originalImage : productImages) {
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
                                        final ProductType type,
                                        final String code,
                                        final String name,
                                        final String categoryCode,
                                        final String boardCode,
                                        final String structureCode,
                                        final ProductStockStatus productStockStatus,
                                        final BigDecimal unitPriceFrom,
                                        final BigDecimal unitPriceTo,
                                        final BigDecimal thicknessValue,
                                        final String thicknessUnit,
                                        final Boolean orientation,
                                        final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "search-field", v));
        Optional.ofNullable(type).ifPresent(v -> addToParams(params, "type", v.name()));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        Optional.ofNullable(categoryCode).ifPresent(v -> addToParams(params, "category-code", v));
        Optional.ofNullable(boardCode).ifPresent(v -> addToParams(params, "board-code", v));
        Optional.ofNullable(structureCode).ifPresent(v -> addToParams(params, "structure-code", v));
        Optional.ofNullable(productStockStatus).ifPresent(v -> addToParams(params, "stock-status", v.name()));
        Optional.ofNullable(unitPriceFrom).ifPresent(v -> addToParams(params, "unit-price-from", v.toPlainString()));
        Optional.ofNullable(unitPriceTo).ifPresent(v -> addToParams(params, "unit-price-to", v.toPlainString()));
        Optional.ofNullable(thicknessValue).ifPresent(v -> addToParams(params, "thickness-value", v.toPlainString()));
        Optional.ofNullable(thicknessUnit).ifPresent(v -> addToParams(params, "thickness-unit", v));
        Optional.ofNullable(orientation).ifPresent(v -> addToParams(params, "orientation", v.toString()));
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

    private List<ApplicationImageWeb> getProductImages(final HttpHeaders headers, final Long id) {
        final ResponseEntity<ApplicationImageWeb[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-images", Map.of("id", Long.toString(id))),
                HttpMethod.GET, new HttpEntity<>(headers),
                ApplicationImageWeb[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
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

    private ApplicationImageWeb setProductImage(final String token, final Long id, final String fileName) {
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

        final ResponseEntity<ApplicationImageWeb> uploadedImage = restTemplate.exchange(
                getURI("/products/{id}/product-images", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageWeb.class
        );
        assertThat(uploadedImage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uploadedImage.getBody()).isNotNull();
        assertThat(uploadedImage.hasBody()).isTrue();
        assertThat(uploadedImage.getBody().fileName()).isEqualTo(fileName);
        assertThat(uploadedImage.getBody().thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();
        return uploadedImage.getBody();
    }

    public void deleteProductImage(final HttpHeaders headers, final Long id, final String fileName) {
        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/products/{id}/product-images/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public List<ProductUnitPriceSo> getProductUnitPrices(final HttpHeaders headers, final Long id) {
        final ResponseEntity<ProductUnitPriceSo[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-unit-prices", Map.of("id", Long.toString(id))),
                HttpMethod.GET, new HttpEntity<>(headers),
                ProductUnitPriceSo[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    public List<ProductUnitPriceSo> setProductUnitPrices(final HttpHeaders headers, final Long id, final List<ProductUnitPriceSo> productUnitPrices) {
        final ResponseEntity<ProductUnitPriceSo[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-unit-prices", Map.of("id", id.toString())),
                HttpMethod.POST,
                new HttpEntity<>(new SingleValueBody<>(productUnitPrices), headers),
                ProductUnitPriceSo[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    public List<Long> getProductCategoryIds(final HttpHeaders headers, final Long id) {
        final ResponseEntity<Long[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-category-ids", Map.of("id", Long.toString(id))),
                HttpMethod.GET, new HttpEntity<>(headers),
                Long[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    public List<Long> setProductCategoryIds(final HttpHeaders headers, final Long id, final List<Long> productCategoryIds) {
        final ResponseEntity<Long[]> response = restTemplate.exchange(
                getURI("/products/{id}/product-category-ids", Map.of("id", id.toString())),
                HttpMethod.POST,
                new HttpEntity<>(new SingleValueBody<>(productCategoryIds), headers),
                Long[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }
}
