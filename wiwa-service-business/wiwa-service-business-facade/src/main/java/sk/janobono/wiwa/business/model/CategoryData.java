package sk.janobono.wiwa.business.model;

import lombok.Builder;

@Builder
public record CategoryData(Long id, String code, String name) {
}
