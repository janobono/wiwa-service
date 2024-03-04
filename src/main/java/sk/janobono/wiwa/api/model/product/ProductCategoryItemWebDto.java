package sk.janobono.wiwa.api.model.product;

public record ProductCategoryItemWebDto(Long id, String code, String name, ProductCategoryWebDto category) {
}
