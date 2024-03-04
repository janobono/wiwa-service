package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.product.*;
import sk.janobono.wiwa.business.model.product.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {ApplicationImageWebMapper.class})
public interface ProductWebMapper {
    ProductCategoryWebDto mapToWebDto(ProductCategoryData productCategory);

    ProductCategoryChangeData mapToData(ProductCategoryChangeWebDto productCategoryChange);

    ProductCategoryItemWebDto mapToWebDto(ProductCategoryItemData productCategoryItem);

    ProductCategoryItemChangeData mapToData(ProductCategoryItemChangeWebDto productCategoryItemChange);

    ProductWebDto mapToWebDto(ProductData product);

    ProductAttributeWebDto mapToWebDto(ProductAttributeData productAttribute);

    ProductQuantityWebDto mapToWebDto(ProductQuantityData productQuantity);

    ProductUnitPriceWebDto mapToWebDto(ProductUnitPriceData productUnitPrice);

    ProductChangeData mapToData(ProductChangeWebDto productChange);

    ProductUnitPriceChangeData mapToData(ProductUnitPriceChangeWebDto productUnitPriceChange);
}
