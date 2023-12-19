package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CodeListDo {
    private Long id;
    private String code;
    private String name;
}
