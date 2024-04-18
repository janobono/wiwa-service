package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface EdgeImageRepository {

    void deleteById(Long id);

    List<ApplicationImageInfoDo> findAllByEdgeId(Long edgeId);

    Optional<EdgeImageDo> findByEdgeIdAndFileName(Long edgeId, String fileName);

    EdgeImageDo save(EdgeImageDo edgeImageDo);
}
