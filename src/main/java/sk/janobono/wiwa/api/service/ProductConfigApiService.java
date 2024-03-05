package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ProductWebMapper;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryWebDto;
import sk.janobono.wiwa.business.service.ProductConfigService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductConfigApiService {

    private final ProductConfigService productConfigService;
    private final ProductWebMapper productWebMapper;

    public SingleValueBodyWebDto<BigDecimal> getVatRate() {
        return new SingleValueBodyWebDto<>(productConfigService.getVatRate());
    }

    public SingleValueBodyWebDto<BigDecimal> setVatRate(final SingleValueBodyWebDto<BigDecimal> singleValueBody) {
        return new SingleValueBodyWebDto<>(productConfigService.setVatRate(singleValueBody.value()));
    }

    public List<ProductCategoryWebDto> getProductCategories() {
        return productConfigService.getProductCategories().stream().map(productWebMapper::mapToWebDto).toList();
    }

    public List<ProductCategoryWebDto> setProductCategories(final List<ProductCategoryChangeWebDto> data) {
        return productConfigService.setProductCategories(data.stream().map(productWebMapper::mapToData).toList())
                .stream().map(productWebMapper::mapToWebDto).toList();
    }

    public ProductCategoryItemWebDto getBoardCategoryItem() {
        return productWebMapper.mapToWebDto(productConfigService.getBoardCategoryItem());
    }

    public ProductCategoryItemWebDto setBoardCategoryItem(final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productWebMapper.mapToWebDto(productConfigService.setBoardCategoryItem(productWebMapper.mapToData(productCategoryItemChange)));
    }

    public ProductCategoryItemWebDto getEdgeCategoryItem() {
        return productWebMapper.mapToWebDto(productConfigService.getEdgeCategoryItem());
    }

    public ProductCategoryItemWebDto setEdgeCategoryItem(final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productWebMapper.mapToWebDto(productConfigService.setEdgeCategoryItem(productWebMapper.mapToData(productCategoryItemChange)));
    }

    public ProductCategoryItemWebDto getServiceCategoryItem() {
        return productWebMapper.mapToWebDto(productConfigService.getServiceCategoryItem());
    }

    public ProductCategoryItemWebDto setServiceCategoryItem(final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productWebMapper.mapToWebDto(productConfigService.setServiceCategoryItem(productWebMapper.mapToData(productCategoryItemChange)));
    }

    public ProductCategoryItemWebDto getFreeSaleCategoryItem() {
        return productWebMapper.mapToWebDto(productConfigService.getFreeSaleCategoryItem());
    }

    public ProductCategoryItemWebDto setFreeSaleCategoryItem(final ProductCategoryItemChangeWebDto productCategoryItemChange) {
        return productWebMapper.mapToWebDto(productConfigService.setFreeSaleCategoryItem(productWebMapper.mapToData(productCategoryItemChange)));
    }

    public List<ProductCategoryItemWebDto> getSearchItems() {
        return productConfigService.getSearchItems().stream()
                .map(productWebMapper::mapToWebDto)
                .toList();
    }

    public List<ProductCategoryItemWebDto> setSearchItems(final List<ProductCategoryItemChangeWebDto> data) {
        return productConfigService.setSearchItems(data.stream().map(productWebMapper::mapToData).toList())
                .stream()
                .map(productWebMapper::mapToWebDto)
                .toList();
    }
}
