package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryWebDto;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductConfigControllerTest extends BaseControllerTest {

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // Vat rate
        final var newVatRate = setVatRate(headers, BigDecimal.valueOf(25L));
        assertThat(newVatRate).isEqualTo(getVatRate(headers));

        // Product categories
        final CodeListDo boardCategory = codeListRepository.save(CodeListDo.builder()
                .code("BOARD")
                .name("board")
                .build());

        final CodeListDo edgeCategory = codeListRepository.save(CodeListDo.builder()
                .code("EDGE")
                .name("edge")
                .build());

        final CodeListDo freeSaleCategory = codeListRepository.save(CodeListDo.builder()
                .code("FREE_SALE")
                .name("free sale")
                .build());

        final var newProductCategories = setProductCategories(headers, List.of(
                new ProductCategoryChangeWebDto(boardCategory.getId()),
                new ProductCategoryChangeWebDto(edgeCategory.getId()),
                new ProductCategoryChangeWebDto(freeSaleCategory.getId())
        ));
        assertThat(newProductCategories).isEqualTo(getProductCategories(headers));

        // Board
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

        final var boardCategoryItem = setBoardCategoryItem(headers, new ProductCategoryItemChangeWebDto(boardCategory.getId(), boardCategoryItem01.getId()));
        assertThat(boardCategoryItem).isEqualTo(getBoardCategoryItem(headers));
        final var searchBoardItems = setBoardSearchItems(headers, List.of(
                new ProductCategoryItemChangeWebDto(boardCategory.getId(), boardCategoryItem01.getId()),
                new ProductCategoryItemChangeWebDto(boardCategory.getId(), boardCategoryItem02.getId())
        ));
        assertThat(searchBoardItems.size()).isEqualTo(2);

        // edge
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

        final var edgeCategoryItem = setEdgeCategoryItem(headers, new ProductCategoryItemChangeWebDto(edgeCategory.getId(), edgeCategoryItem01.getId()));
        assertThat(edgeCategoryItem).isEqualTo(getEdgeCategoryItem(headers));
        final var searchEdgeItems = setEdgeSearchItems(headers, List.of(
                new ProductCategoryItemChangeWebDto(edgeCategory.getId(), edgeCategoryItem01.getId()),
                new ProductCategoryItemChangeWebDto(edgeCategory.getId(), edgeCategoryItem02.getId())
        ));
        assertThat(searchEdgeItems.size()).isEqualTo(2);

        // free sale
        final CodeListItemDo freeSaleCategoryItem01 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(freeSaleCategory.getId())
                .treeCode("FS01")
                .code("FS01")
                .value("free sale item 01")
                .sortNum(1)
                .build());
        final CodeListItemDo freeSaleCategoryItem02 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(freeSaleCategory.getId())
                .treeCode("FS02")
                .code("FS02")
                .value("free sale item 02")
                .sortNum(2)
                .build());

        final var freeSaleCategoryItem = setFreeSaleCategoryItem(headers, new ProductCategoryItemChangeWebDto(freeSaleCategory.getId(), freeSaleCategoryItem01.getId()));
        assertThat(freeSaleCategoryItem).isEqualTo(getFreeSaleCategoryItem(headers));
        final var searchFreeSaleItems = setFreeSaleSearchItems(headers, List.of(
                new ProductCategoryItemChangeWebDto(edgeCategory.getId(), edgeCategoryItem01.getId()),
                new ProductCategoryItemChangeWebDto(edgeCategory.getId(), edgeCategoryItem02.getId())
        ));
        assertThat(searchFreeSaleItems.size()).isEqualTo(2);
    }

    private BigDecimal getVatRate(final HttpHeaders headers) {
        final ResponseEntity<ObjectNode> response = restTemplate.exchange(
                getURI("/product-config/vat-rate"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ObjectNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().get("value").decimalValue();
    }

    private BigDecimal setVatRate(final HttpHeaders headers, final BigDecimal vatRate) {
        final ResponseEntity<ObjectNode> response = restTemplate.exchange(
                getURI("/product-config/vat-rate"),
                HttpMethod.POST,
                new HttpEntity<>(new SingleValueBodyWebDto<>(vatRate), headers),
                ObjectNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().get("value").decimalValue();
    }

    private List<ProductCategoryWebDto> getProductCategories(final HttpHeaders headers) {
        final ResponseEntity<ProductCategoryWebDto[]> response = restTemplate.exchange(
                getURI("/product-config/product-categories"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductCategoryWebDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private List<ProductCategoryWebDto> setProductCategories(final HttpHeaders headers, final List<ProductCategoryChangeWebDto> data) {
        final ResponseEntity<ProductCategoryWebDto[]> response = restTemplate.exchange(
                getURI("/product-config/product-categories"),
                HttpMethod.POST,
                new HttpEntity<>(data, headers),
                ProductCategoryWebDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private ProductCategoryItemWebDto getBoardCategoryItem(final HttpHeaders headers) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/board-category-item"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private ProductCategoryItemWebDto setBoardCategoryItem(final HttpHeaders headers, final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/board-category-item"),
                HttpMethod.POST,
                new HttpEntity<>(productCategoryItemChange, headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private List<ProductCategoryItemWebDto> setBoardSearchItems(final HttpHeaders headers, final List<ProductCategoryItemChangeWebDto> data) {
        final ResponseEntity<ProductCategoryItemWebDto[]> response = restTemplate.exchange(
                getURI("/product-config/board-search-items"),
                HttpMethod.POST,
                new HttpEntity<>(data, headers),
                ProductCategoryItemWebDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private ProductCategoryItemWebDto getEdgeCategoryItem(final HttpHeaders headers) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/edge-category-item"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private ProductCategoryItemWebDto setEdgeCategoryItem(final HttpHeaders headers, final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/edge-category-item"),
                HttpMethod.POST,
                new HttpEntity<>(productCategoryItemChange, headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private List<ProductCategoryItemWebDto> setEdgeSearchItems(final HttpHeaders headers, final List<ProductCategoryItemChangeWebDto> data) {
        final ResponseEntity<ProductCategoryItemWebDto[]> response = restTemplate.exchange(
                getURI("/product-config/edge-search-items"),
                HttpMethod.POST,
                new HttpEntity<>(data, headers),
                ProductCategoryItemWebDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }

    private ProductCategoryItemWebDto getFreeSaleCategoryItem(final HttpHeaders headers) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/free-sale-category-item"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private ProductCategoryItemWebDto setFreeSaleCategoryItem(final HttpHeaders headers, final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        final ResponseEntity<ProductCategoryItemWebDto> response = restTemplate.exchange(
                getURI("/product-config/free-sale-category-item"),
                HttpMethod.POST,
                new HttpEntity<>(productCategoryItemChange, headers),
                ProductCategoryItemWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private List<ProductCategoryItemWebDto> setFreeSaleSearchItems(final HttpHeaders headers, final List<ProductCategoryItemChangeWebDto> data) {
        final ResponseEntity<ProductCategoryItemWebDto[]> response = restTemplate.exchange(
                getURI("/product-config/free-sale-search-items"),
                HttpMethod.POST,
                new HttpEntity<>(data, headers),
                ProductCategoryItemWebDto[].class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return Arrays.asList(response.getBody());
    }
}
