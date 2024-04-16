package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;

import java.util.Optional;

public interface UserRepository {

    int count();

    void deleteById(final Long id);

    boolean existsByEmail(final String email);

    boolean existsById(final Long id);

    boolean existsByUsername(final String username);

    Page<UserDo> findAll(final UserSearchCriteriaDo criteria, final Pageable pageable);

    Optional<UserDo> findByEmail(final String email);

    Optional<UserDo> findById(final Long id);

    Optional<UserDo> findByUsername(final String username);

    UserDo save(final UserDo userDo);
}
