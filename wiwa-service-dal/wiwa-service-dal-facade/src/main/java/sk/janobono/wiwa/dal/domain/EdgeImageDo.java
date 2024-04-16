package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"thumbnail", "data"})
public class EdgeImageDo {
    private Long id;
    private Long edgeId;
    private String fileName;
    private String fileType;
    private byte[] thumbnail;
    private byte[] data;
}
