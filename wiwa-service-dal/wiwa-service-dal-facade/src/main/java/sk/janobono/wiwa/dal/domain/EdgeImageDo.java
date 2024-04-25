package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"data"})
public class EdgeImageDo {
    private Long edgeId;
    private String fileType;
    private byte[] data;
}
