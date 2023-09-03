package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.UserDo;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDo, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserDo> findByEmail(String email);

    Optional<UserDo> findByUsername(String username);

    Page<UserDo> findAll(Specification<UserDo> specification, Pageable pageable);

    @Modifying
    @Query("delete from UserDo u where u.id=?1")
    void deleteById(Long id);
}
