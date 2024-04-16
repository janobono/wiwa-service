package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.model.Authority;

import java.util.List;

public interface AuthorityRepository {

    int count();

    List<AuthorityDo> findAll();

    List<AuthorityDo> findByUserId(final Long userId);

    AuthorityDo save(final AuthorityDo authorityDo);

    void saveUserAuthorities(final Long userId, final List<Authority> authorities);
}
