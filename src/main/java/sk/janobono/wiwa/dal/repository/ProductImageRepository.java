package sk.janobono.wiwa.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sk.janobono.wiwa.dal.domain.ProductImageDo;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageDo, Long> {
    List<ProductImageDo> findAllByProductId(Long productId);

    Optional<ProductImageDo> findByProductIdAndFileName(Long productId, String fileName);

    @Modifying
    @Query("delete from ProductImageDo pi where pi.id=?1")
    void deleteById(Long id);
}
