package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.edge.EdgeCategoryItemChangeData;
import sk.janobono.wiwa.business.model.edge.EdgeChangeData;
import sk.janobono.wiwa.business.model.edge.EdgeData;
import sk.janobono.wiwa.business.model.edge.EdgeSearchCriteriaData;

import java.util.List;

public interface EdgeService {

    Page<EdgeData> getEdges(final EdgeSearchCriteriaData criteria, final Pageable pageable);

    EdgeData getEdge(final Long id);

    EdgeData addEdge(final EdgeChangeData data);

    EdgeData setEdge(final Long id, final EdgeChangeData data);

    void deleteEdge(final Long id);

    EdgeData setEdgeImage(final Long edgeId, final MultipartFile multipartFile);

    EdgeData deleteEdgeImage(final Long edgeId, final String fileName);

    EdgeData setEdgeCategoryItems(final Long edgeId, final List<EdgeCategoryItemChangeData> categoryItems);
}
