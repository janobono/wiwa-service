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

    Page<EdgeData> getEdges(EdgeSearchCriteriaData criteria, Pageable pageable);

    EdgeData getEdge(Long id);

    EdgeData addEdge(EdgeChangeData data);

    EdgeData setEdge(Long id, EdgeChangeData data);

    void deleteEdge(Long id);

    EdgeData setEdgeImage(Long edgeId, MultipartFile multipartFile);

    EdgeData deleteEdgeImage(Long edgeId, String fileName);

    EdgeData setEdgeCategoryItems(Long edgeId, List<EdgeCategoryItemChangeData> categoryItems);
}
