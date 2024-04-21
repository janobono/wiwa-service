package sk.janobono.wiwa.business.model.application;

public record ApplicationImageData(String fileName, String fileType, byte[] thumbnail, byte[] data) {
}
