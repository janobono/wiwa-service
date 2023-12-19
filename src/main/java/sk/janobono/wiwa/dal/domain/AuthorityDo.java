package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Authority;

@Builder
@Data
public class AuthorityDo {
    private Long id;
    private Authority authority;
}
