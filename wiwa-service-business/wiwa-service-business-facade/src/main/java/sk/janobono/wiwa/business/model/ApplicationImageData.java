package sk.janobono.wiwa.business.model;

public record ApplicationImageData(String fileName, String fileType, byte[] thumbnail, byte[] data) {
}
