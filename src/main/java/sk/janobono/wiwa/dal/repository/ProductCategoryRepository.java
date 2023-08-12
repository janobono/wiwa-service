package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.janobono.wiwa.dal.domain.ProductCategoryDo;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryDo, Long> {

    int countByCode(String code);

    int countByIdNotAndCode(Long id, String code);

    int countByParentId(Long parentId);

    int countByParentIdNull();

    Page<ProductCategoryDo> findAll(Specification<ProductCategoryDo> specification, Pageable pageable);
}
