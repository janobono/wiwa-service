package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"thumbnail", "data"})
public class BoardImageDo {
    private Long id;
    private Long boardId;
    private String fileName;
    private String fileType;
    private byte[] thumbnail;
    private byte[] data;
}
