package sk.janobono.wiwa.dal.model;

public record OrderItemInfoDo(
        String name,
        String description,
        Integer quantity
) {
}
