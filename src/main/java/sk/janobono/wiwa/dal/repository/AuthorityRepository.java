package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.model.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityDo, Long> {
    Optional<AuthorityDo> findByAuthority(Authority authority);

    @Modifying
    @Query("delete from AuthorityDo a where a.id=?1")
    void deleteById(Long id);
}
