package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.ProductWebMapper;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductUnitPriceChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductWebDto;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaData;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductApiService {

    private final ProductService productService;
    private final ProductWebMapper productWebMapper;

    public Page<ProductWebDto> getProducts(final String searchField, final String code, final String name,
                                           final ProductStockStatus stockStatus, final List<String> codeListItems,
                                           final Pageable pageable) {
        final ProductSearchCriteriaData criteria = ProductSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .stockStatus(stockStatus)
                .codeListItems(codeListItems)
                .build();
        return productService.getProducts(criteria, pageable).map(productWebMapper::mapToWebDto);
    }

    public ProductWebDto getProduct(final Long id) {
        return productWebMapper.mapToWebDto(productService.getProduct(id));
    }

    public ProductWebDto addProduct(final ProductChangeWebDto productChange) {
        return productWebMapper.mapToWebDto(productService.addProduct(productWebMapper.mapToData(productChange)));
    }

    public ProductWebDto setProduct(final Long id, final ProductChangeWebDto productChange) {
        return productWebMapper.mapToWebDto(productService.setProduct(id, productWebMapper.mapToData(productChange)));
    }

    public void deleteProduct(final Long id) {
        productService.deleteProduct(id);
    }

    public ProductWebDto setProductImage(final Long id, final MultipartFile multipartFile) {
        return productWebMapper.mapToWebDto(productService.setProductImage(id, multipartFile));
    }

    public ProductWebDto deleteProductImage(final Long id, final String fileName) {
        return productWebMapper.mapToWebDto(productService.deleteProductImage(id, fileName));
    }

    public ProductWebDto setProductUnitPrices(final Long id, final List<ProductUnitPriceChangeWebDto> productUnitPrices) {
        return productWebMapper.mapToWebDto(
                productService.setProductUnitPrices(id, productUnitPrices.stream().map(productWebMapper::mapToData).toList())
        );
    }

    public ProductWebDto setProductCategoryItems(final Long id, final List<ProductCategoryItemChangeWebDto> categoryItems) {
        return productWebMapper.mapToWebDto(
                productService.setProductCategoryItems(id, categoryItems.stream().map(productWebMapper::mapToData).toList())
        );
    }
}
