package sk.janobono.wiwa.common.model;

import org.springframework.core.io.Resource;

public record ResourceEntitySo(String fileName, String contentType, Resource resource) {
    @Override
    public String toString() {
        return "ResourceEntitySo{" +
                "fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
