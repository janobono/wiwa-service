package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;

import java.util.Optional;

public interface UserRepository {

    int count();

    void deleteById(Long id);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    boolean existsByUsername(String username);

    Page<UserDo> findAll(UserSearchCriteriaDo criteria, Pageable pageable);

    Optional<UserDo> findByEmail(String email);

    Optional<UserDo> findById(Long id);

    Optional<UserDo> findByUsername(String username);

    UserDo save(UserDo userDo);
}
