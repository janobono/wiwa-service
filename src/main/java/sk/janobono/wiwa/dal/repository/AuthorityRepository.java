package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.model.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityDo, Long> {
    Optional<AuthorityDo> findByAuthority(Authority authority);
}
