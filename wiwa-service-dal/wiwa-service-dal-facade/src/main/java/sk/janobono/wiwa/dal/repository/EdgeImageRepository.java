package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.EdgeImageDo;

import java.util.Optional;

public interface EdgeImageRepository {

    void deleteByEdgeId(long edgeId);

    Optional<EdgeImageDo> findByEdgeId(long edgeId);

    EdgeImageDo save(EdgeImageDo edgeImageDo);
}
