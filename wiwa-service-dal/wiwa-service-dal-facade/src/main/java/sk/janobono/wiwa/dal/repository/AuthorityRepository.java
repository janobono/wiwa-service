package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

public interface AuthorityRepository {

    int count();

    List<AuthorityDo> findAll();

    List<AuthorityDo> findByUserId(Long userId);

    AuthorityDo save(AuthorityDo authorityDo);

    void saveUserAuthorities(Long userId, List<Authority> authorities);
}
