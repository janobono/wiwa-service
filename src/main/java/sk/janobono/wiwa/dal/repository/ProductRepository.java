package sk.janobono.wiwa.dal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ProductDo;

public interface ProductRepository extends JpaRepository<ProductDo, Long> {
    int countByCode(String code);

    int countByIdNotAndCode(Long id, String code);

    Page<ProductDo> findAll(Specification<ProductDo> specification, Pageable pageable);

    @Modifying
    @Query("delete from ProductDo p where p.id=?1")
    void deleteById(Long id);
}

