package sk.janobono.wiwa.model;

import org.springframework.core.io.Resource;

public record ResourceEntity(String fileName, String contentType, Resource resource) {
    @Override
    public String toString() {
        return "ResourceEntity{" +
                "fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
