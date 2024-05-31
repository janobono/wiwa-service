package sk.janobono.wiwa.business.model.board;

import lombok.Builder;

@Builder
public record BoardCategoryData(Long id, String code, String name) {
}
