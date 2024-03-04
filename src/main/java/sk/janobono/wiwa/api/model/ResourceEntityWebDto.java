package sk.janobono.wiwa.api.model;

import org.springframework.core.io.Resource;

public record ResourceEntityWebDto(String fileName, String contentType, Resource resource) {
}
