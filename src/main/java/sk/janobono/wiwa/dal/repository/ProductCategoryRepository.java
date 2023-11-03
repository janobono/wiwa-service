package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ProductCategoryDo;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryDo, Long> {

    int countByCode(String code);

    int countByIdNotAndCode(Long id, String code);

    int countByParentId(Long parentId);

    int countByParentIdNull();

    Page<ProductCategoryDo> findAll(Specification<ProductCategoryDo> specification, Pageable pageable);

    @Modifying
    @Query("delete from ProductCategoryDo pc where pc.id=?1")
    void deleteById(Long id);
}