package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString(exclude = {"password", "authorities"})
@Entity
@Table(name = "wiwa_user")
public class UserDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "title_before")
    private String titleBefore;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "mid_name")
    private String midName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title_after")
    private String titleAfter;

    @Column(name = "email")
    private String email;

    @Column(name = "gdpr")
    private boolean gdpr;

    @Column(name = "confirmed")
    private boolean confirmed;

    @Column(name = "enabled")
    private boolean enabled;

    @OneToMany
    @JoinTable(
            name = "wiwa_user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")}
    )
    private Set<AuthorityDo> authorities;

    public Set<AuthorityDo> getAuthorities() {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        return authorities;
    }
}
