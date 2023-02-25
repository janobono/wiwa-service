package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.common.model.Authority;

public interface AuthorityRepository {
    long count();

    long addAuthority(Authority authority);
}
