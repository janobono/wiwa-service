package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.janobono.wiwa.model.Authority;

@EqualsAndHashCode(of = "authority")
@Getter
@Setter
@ToString
@Entity
@Table(name = "wiwa_authority")
public class AuthorityDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;
}
