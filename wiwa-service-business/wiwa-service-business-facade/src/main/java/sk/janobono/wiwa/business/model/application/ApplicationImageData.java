package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record ApplicationImageData(String fileName, String fileType, byte[] thumbnail, byte[] data) {
}
