package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"data"})
public class BoardImageDo {
    private Long boardId;
    private String fileType;
    private byte[] data;
}
