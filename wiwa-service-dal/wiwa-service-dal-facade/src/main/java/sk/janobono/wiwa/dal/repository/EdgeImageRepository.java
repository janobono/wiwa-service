package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface EdgeImageRepository {

    void deleteById(long id);

    List<ApplicationImageInfoDo> findAllByEdgeId(long edgeId);

    Optional<EdgeImageDo> findByEdgeIdAndFileName(long edgeId, String fileName);

    EdgeImageDo save(EdgeImageDo edgeImageDo);
}
