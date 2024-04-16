package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApplicationPropertyDo {
    private String key;
    private String value;
}
