package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ProductAttributeDo;

public interface ProductAttributeRepository extends JpaRepository<ProductAttributeDo, Long> {
    @Modifying
    @Query("delete from ProductAttributeDo pa where pa.productId=?1")
    void deleteByProductId(Long productId);

    @Modifying
    @Query("delete from ProductAttributeDo pa where pa.id=?1")
    void deleteById(Long id);
}
