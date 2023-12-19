package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString(exclude = {"password"})
public class UserDo {
    private Long id;
    private String username;
    private String password;
    private String titleBefore;
    private String firstName;
    private String midName;
    private String lastName;
    private String titleAfter;
    private String email;
    private boolean gdpr;
    private boolean confirmed;
    private boolean enabled;
}
