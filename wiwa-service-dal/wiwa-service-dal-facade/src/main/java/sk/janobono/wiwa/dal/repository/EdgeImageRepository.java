package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;

import java.util.List;
import java.util.Optional;

public interface EdgeImageRepository {

    void deleteById(final Long id);

    List<ApplicationImageInfoDo> findAllByEdgeId(final Long edgeId);

    Optional<EdgeImageDo> findByEdgeIdAndFileName(final Long edgeId, final String fileName);

    EdgeImageDo save(final EdgeImageDo edgeImageDo);
}
