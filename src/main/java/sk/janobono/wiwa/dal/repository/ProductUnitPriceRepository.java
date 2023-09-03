package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ProductUnitPriceDo;

public interface ProductUnitPriceRepository extends JpaRepository<ProductUnitPriceDo, Long> {
    @Modifying
    @Query("delete from ProductUnitPriceDo pu where pu.productId=?1")
    void deleteByProductId(Long productId);

    @Modifying
    @Query("delete from ProductUnitPriceDo pu where pu.id=?1")
    void deleteById(Long id);
}
