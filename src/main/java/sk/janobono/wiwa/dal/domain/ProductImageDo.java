package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"thumbnail", "data"})
public class ProductImageDo {
    private Long id;
    private Long productId;
    private String fileName;
    private String fileType;
    private byte[] thumbnail;
    private byte[] data;
}
