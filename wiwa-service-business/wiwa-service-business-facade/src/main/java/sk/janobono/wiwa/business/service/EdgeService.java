package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.CategoryItemChangeData;
import sk.janobono.wiwa.business.model.edge.EdgeChangeData;
import sk.janobono.wiwa.business.model.edge.EdgeData;
import sk.janobono.wiwa.business.model.edge.EdgeSearchCriteriaData;

import java.util.List;

public interface EdgeService {

    Page<EdgeData> getEdges(EdgeSearchCriteriaData criteria, Pageable pageable);

    EdgeData getEdge(long id);

    EdgeData addEdge(EdgeChangeData data);

    EdgeData setEdge(long id, EdgeChangeData data);

    void deleteEdge(long id);

    void setEdgeImage(long edgeId, MultipartFile multipartFile);

    void deleteEdgeImage(long edgeId);

    EdgeData setEdgeCategoryItems(long edgeId, List<CategoryItemChangeData> categoryItems);
}
