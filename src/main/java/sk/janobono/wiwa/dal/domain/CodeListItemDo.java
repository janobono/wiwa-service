package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CodeListItemDo {
    private Long id;
    private Long codeListId;
    private Long parentId;
    private String treeCode;
    private String code;
    private String value;
    private Integer sortNum;
}
