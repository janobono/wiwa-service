package sk.janobono.wiwa.dal.domain;

public record ApplicationImageDo(String fileName, String fileType, byte[] thumbnail, byte[] data) {
}
